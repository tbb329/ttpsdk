package com.zlst.module.task.service;
import com.zlst.module.core.SdkInterface;
import com.zlst.utils.SpringUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zlst.common.config.GlobalCacheManage;
import com.zlst.module.task.bean.AgreementInfo;
import com.zlst.module.task.bean.DispatchInfo;
import com.zlst.module.task.bean.PointInfo;
import com.zlst.module.task.dto.CollectSourceDto;
import com.zlst.module.task.dto.ConnectDto;
import com.zlst.module.task.dto.DispatchDto;
import com.zlst.module.task.dto.GroupDto;
import com.zlst.param.ObjectToResult;
import com.zlst.param.Result;
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
    private static final ConcurrentHashMap<String, ScheduledFuture> collectThreadTask = new ConcurrentHashMap<>(8);

    /**
     * 运行中采集分组
     */
    private static final ConcurrentHashMap<String, DispatchInfo> executingCollectGroup = new ConcurrentHashMap<>(8);

    /**
     * 停止中采集分组
     */
    public static final ConcurrentHashMap<String, DispatchInfo> suspendCollectGroup = new ConcurrentHashMap<>(8);

    public static final ConcurrentHashMap<String, SdkInterface> statusMap = new ConcurrentHashMap<>();

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
        if(checkFlag && !BeanUtil.isEmpty(collectThreadTask.get(collectSourceDto.getCollectSourceId()))){
            logger.error("CollectQueryService.publishedQuery, 已对该采集源生成采集任务,不能再次发布采集源");
            return ObjectToResult.getResult(ObjectToResult.SUCCESS_CODE, "CollectQueryService.publishedQuery, 已对该采集源生成采集任务,不能再次发布采集源");
        }
        AgreementInfo agreementInfo = new AgreementInfo(dispatchDto.getAgreementToExeDto(), collectSourceDto);
        ConnectDto connectToExeDto = dispatchDto.getConnectToExeDto();
        List<GroupDto> groupDtoList = parseAgreement(agreementInfo);
        executingCollectGroup.put(collectSourceDto.getCollectSourceId(), new DispatchInfo(agreementInfo, connectToExeDto, groupDtoList));
        //todo
        String collectType = agreementInfo.getAgreementTypeName();
        SdkInterface sdkInterface = SpringUtils.getBeanByName(collectType);
        sdkInterface.setParam(connectToExeDto,agreementInfo,groupDtoList, collectSourceDto.getCollectSourceId());

        ScheduledFuture scheduledFutures = scheduleThreadPool.schedule(sdkInterface, 0, TimeUnit.SECONDS);

        collectThreadTask.put(collectSourceDto.getCollectSourceId(), scheduledFutures);

        return ObjectToResult.getResult(executingCollectGroup);
    }

    public List<GroupDto> parseAgreement(AgreementInfo agreementInfo){
        logger.info("开始执行解析协议信息!!!!");
        Map<String, String> agreementParam = jsonObjectToHash(JSONObject.parseObject(agreementInfo.getAgreementContent()));
        String groupInfo = agreementParam.get(GlobalCacheManage.GROUP_INFO);
        agreementParam.remove(GlobalCacheManage.GROUP_INFO);
        agreementInfo.setAgreementMap(agreementParam);
        agreementInfo.setAgreementContent(groupInfo);
        return parseToGroupDto(agreementInfo);
    }

    public List<GroupDto> parseToGroupDto(AgreementInfo agreementInfo){
        logger.info("开始解析分组信息!!!!");
        JSONArray groupArray = JSONArray.parseArray(agreementInfo.getAgreementContent());

        List<GroupDto> groupDtoList = groupArray.stream().map( group -> {
            JSONObject groupObject = (JSONObject) JSONObject.toJSON(group);
            GroupDto groupDto = jsonObjectToGroup(groupObject, agreementInfo);
            return groupDto;
        }).collect(toList());

        return groupDtoList;
    }

    public GroupDto jsonObjectToGroup(JSONObject groupObject, AgreementInfo agreementInfo){
        String groupName = groupObject.get(GlobalCacheManage.GROUP_NAME).toString();
        String periodTime = groupObject.get(GlobalCacheManage.PERIOD_TIME).toString();
        HashMap<String, String> params = this.jsonObjectToHash(groupObject, GlobalCacheManage.FILTER_GROUP_ELEMENTS);
        List<PointInfo> pointInfoList = strToPoint(params.get(GlobalCacheManage.POINT_INFO));
        params.remove(GlobalCacheManage.POINT_INFO);
        logger.info("解析分组 :{} 信息结束", groupName);
        return new GroupDto(groupName, periodTime, params, agreementInfo, pointInfoList);
    }

    public List<PointInfo> strToPoint(String pointStr){
        JSONArray pointArray = JSONArray.parseArray(pointStr);
        List<PointInfo> pointInfoList = pointArray.stream().map( point -> {
            JSONObject pointObject = (JSONObject) JSONObject.toJSON(point);
            HashMap<String, String> pointParam = this.jsonObjectToHash(pointObject);
            PointInfo pointInfo = new PointInfo(pointParam);
            return pointInfo;
        }).collect(toList());
        logger.info("解析测点信息结束!!!");
        return pointInfoList;
    }

    public HashMap<String, String> jsonObjectToHash(JSONObject jsonObject, String... filter){
        HashMap<String, String> params = new HashMap<>(8);
        List<String> filterElements = Arrays.asList(filter);
        for(Iterator<?> iterator = jsonObject.keySet().iterator(); iterator.hasNext();){
            String key = iterator.next().toString();
            if(!IterUtil.isEmpty(filterElements) && filterElements.contains(key)){
                continue;
            }
            params.put(key, jsonObject.get(key).toString());
        }
        return params;
    }


    public boolean delQuery(String collectSourceId){
        logger.info("采集源Id是 :{}", collectSourceId);
        ScheduledFuture scheduledFutures = collectThreadTask.get(collectSourceId);
        if(BeanUtil.isEmpty(scheduledFutures)){
            if(BeanUtil.isEmpty(suspendCollectGroup.get(collectSourceId))){
                logger.error("CollectQueryService.delQuery 未在执行中或暂停中的任务列表查询到相关任务，请确认任务是否存在");
                return false;
            }
            suspendCollectGroup.remove(collectSourceId);
            return true;
        }
        scheduledFutures.cancel(false);
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
        return ObjectToResult.getResult(JSONObject.toJSONString(suspendCollectGroup));
    }

    public Result restart(String collectSourceId){
        logger.info("采集源Id是 :{}", collectSourceId);
        DispatchInfo dispatchInfo = suspendCollectGroup.get(collectSourceId);
        suspendCollectGroup.remove(collectSourceId, dispatchInfo);

        return ObjectToResult.getResult(JSONObject.toJSONString(executingCollectGroup));
    }

    public Map getExecutingMap(){
        logger.info("开始获取执行中采集源任务列表!!!");
        return executingCollectGroup;
    }

    public Map getSuspendMap(){
        logger.info("开始获取停止中采集源任务列表!!!");
        return suspendCollectGroup;
    }

    public  void gettatus(String srcId){
        SdkInterface sdkInterface = CollectQueryService.statusMap.get(srcId);
        if(!BeanUtil.isEmpty(sdkInterface)){

            sdkInterface.disconnect();


        }
    }

    public static boolean taskIsActive(String cltId) {
        return executingCollectGroup.containsKey(cltId);
    }

}
