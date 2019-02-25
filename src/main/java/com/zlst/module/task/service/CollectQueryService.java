package com.zlst.module.task.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.StrUtil;

import com.zlst.module.task.bean.AgreementInfo;
import com.zlst.module.task.bean.DispatchInfo;
import com.zlst.module.task.bean.PointInfo;
import com.zlst.module.task.dto.CollectSourceDto;
import com.zlst.module.task.dto.ConnectDto;
import com.zlst.module.task.dto.DispatchDto;
import com.zlst.module.task.dto.GroupDto;
import com.zlst.module.task.utils.AgreementAnalyzeUtil;
import com.zlst.param.ObjectToResult;
import com.zlst.param.Result;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

/**
 * 采集队列服务类服务
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/28
 */
@Service
public class CollectQueryService {

    private static final Logger logger = LoggerFactory.getLogger(CollectQueryService.class);

    /**
     * 延时执行线程池
     */
    private static final ScheduledExecutorService scheduleThreadPool = Executors.newScheduledThreadPool(2);

    /**
     * 运行中采集任务
     */
    private static final ConcurrentHashMap<String, List<ScheduledFuture>> collectThreadTask = new ConcurrentHashMap<>(8);

    /**
     * 运行中采集分组
     */
    private static final ConcurrentHashMap<String, DispatchInfo> executingCollectGroup = new ConcurrentHashMap<>(8);

    /**
     * 停止中采集分组
     */
    public static final ConcurrentHashMap<String, DispatchInfo> suspendCollectGroup = new ConcurrentHashMap<>(8);

    public void publishedQueryList(List<DispatchDto> dispatchDtoList){
        if(IterUtil.isEmpty(dispatchDtoList)){
            return;
        }
        dispatchDtoList.forEach(dispatchDto -> this.publishedQuery(dispatchDto, false));
    }

    /**
     *
     * @param dispatchDto
     * @param checkFlag 初始启动时不检查采集源是否存在，正常发布流程检查
     */
    public Result publishedQuery(DispatchDto dispatchDto, boolean checkFlag){
        CollectSourceDto collectSourceDto = dispatchDto.getColSrcInfoToExeDto();
        if(StrUtil.isBlank(collectSourceDto.getCollectSourceId())){
            logger.error("输入采集源ID为空，请确认输入");
            return ObjectToResult.getResult(5, "输入采集源ID为空，请确认输入");
        }
        logger.info("调用发布采集源任务，当前采集源ID是:{}", collectSourceDto.getCollectSourceId());
        if(checkFlag && IterUtil.isNotEmpty(collectThreadTask.get(collectSourceDto.getCollectSourceId()))){
            logger.error("CollectQueryService.publishedQuery, 已对该采集源生成采集任务,不能再次发布采集源");
            return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "CollectQueryService.publishedQuery, 已对该采集源生成采集任务,不能再次发布采集源");
        }
        AgreementInfo agreementInfo = new AgreementInfo(dispatchDto.getAgreementToExeDto(), collectSourceDto);
        ConnectDto connectToExeDto = dispatchDto.getConnectToExeDto();
        List<GroupDto> groupDtoList = AgreementAnalyzeUtil.parseAgreement(agreementInfo);
        executingCollectGroup.put(collectSourceDto.getCollectSourceId(), new DispatchInfo(agreementInfo, connectToExeDto, groupDtoList));

        GroovyObject ptcObj = null;
//                CollectTaskActuator.createCollectTask(connectToExeDto, agreementInfo.getAgreementTypeName());
        if (null != ptcObj) {
            logger.info("连接创建成功!!!");
//            logger.info("协议解析成功!!!");
            List<ScheduledFuture> scheduledFutures = saveQuery(groupDtoList, ptcObj);
            collectThreadTask.put(collectSourceDto.getCollectSourceId(), scheduledFutures);
        }
        return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "发布采集源任务成功，开始采集!");
    }

    public List<ScheduledFuture> saveQuery(List<GroupDto> groupDtoList, GroovyObject ptcObj){
        List<ScheduledFuture> scheduledFutures = groupDtoList.stream().map(groupDto -> {
            long period = groupDto.getPeriodTime();
            CollectTask collectTask = new CollectTask(groupDto, period, ptcObj);
            ScheduledFuture scheduledFuture = scheduleThreadPool.scheduleAtFixedRate
                                                    (collectTask,  1000, period, TimeUnit.MILLISECONDS);
            return scheduledFuture;
        }).collect(toList());
        logger.info("新建任务成功，开始进行采集!!!");
        return scheduledFutures;
    }

    public boolean delQuery(String collectSourceId){
        logger.info("采集源Id是 :{}", collectSourceId);
        List<ScheduledFuture> scheduledFutures = collectThreadTask.get(collectSourceId);
        if(IterUtil.isEmpty(scheduledFutures)){
            if(BeanUtil.isEmpty(suspendCollectGroup.get(collectSourceId))){
                logger.error("CollectQueryService.delQuery 未在执行中或暂停中的任务列表查询到相关任务，请确认任务是否存在");
                return false;
            }
            suspendCollectGroup.remove(collectSourceId);
            return true;
        }
        scheduledFutures.stream().forEach(scheduledFuture -> scheduledFuture.cancel(false));
        collectThreadTask.remove(collectSourceId);
        executingCollectGroup.remove(collectSourceId);
        return true;
    }

    public Result stopQuery(String collectSourceId){
        logger.info("采集源Id是 :{}", collectSourceId);
        DispatchInfo dispatchInfo = executingCollectGroup.get(collectSourceId);
        if(BeanUtil.isEmpty(dispatchInfo) || IterUtil.isEmpty(dispatchInfo.getGroupDtoList())){
            logger.error("CollectQueryService.stopQuery 未查询到执行中的任务，请确认，任务是否已发布并执行采集");
            return ObjectToResult.getResult(8, "CollectQueryService.stopQuery 未查询到执行中的任务，请确认，任务是否已发布并执行采集");
        }
        suspendCollectGroup.put(collectSourceId, dispatchInfo);
        if(!delQuery(collectSourceId)){
            return ObjectToResult.getResult(7, "CollectQueryService.delQuery 未在执行中或暂停中的任务列表查询到相关任务，请确认任务是否存在");
        }
        return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "停止任务成功!");
    }

    public Result restart(String collectSourceId){
        logger.info("采集源Id是 :{}", collectSourceId);
        DispatchInfo dispatchInfo = suspendCollectGroup.get(collectSourceId);
        suspendCollectGroup.remove(collectSourceId, dispatchInfo);
        GroovyObject ptcObj = null;
//                CollectTaskActuator.createCollectTask(dispatchInfo.getConnectDto(), dispatchInfo.getAgreementInfo().getAgreementTypeName());
        if (null != ptcObj) {
            List<ScheduledFuture> scheduledFutures = saveQuery(dispatchInfo.getGroupDtoList(), ptcObj);
            collectThreadTask.put(collectSourceId, scheduledFutures);
            executingCollectGroup.put(collectSourceId, dispatchInfo);
        }
        return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "重启任务成功!");
    }

    public Map getExecutingMap(){
        logger.info("开始获取执行中采集源任务列表!!!");
        return executingCollectGroup;
    }

    public Map getSuspendMap(){
        logger.info("开始获取停止中采集源任务列表!!!");
        return suspendCollectGroup;
    }

    public static boolean taskIsActive(String cltId) {
        return executingCollectGroup.containsKey(cltId);
    }

}
