//package com.zlst.module.task.service;
//
//import cn.hutool.core.collection.IterUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.zlst.common.http.CallUipUtil;
//import com.zlst.module.task.bean.AgreementInfo;
//import com.zlst.module.task.bean.DispatchInfo;
//import com.zlst.module.task.dto.DispatchDto;
//import com.zlst.module.task.dto.GroupDto;
//import com.zlst.module.task.enums.ColInstanceStatus;
//import com.zlst.module.task.utils.AgreementAnalyzeUtil;
//import com.zlst.param.ObjectToResult;
//import com.zlst.param.Result;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
//import static java.util.stream.Collectors.groupingBy;
//
///**
// /**
// * 服务重启后尝试拉取调度配置线程
// *
// * Created by 170253 on 2019/1/21
// */
//@Component
//@Order(value = 2)
//public class  ReConnectThread  implements CommandLineRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(ReConnectThread.class);
//
//    /** 周期为6分钟 */
//    private static final int period = 6 * 60 * 1000;
//
//    /** 一共进行重连尝试5次 */
//    private static final int time = 5;
//
//    private static List<DispatchDto> dispatchDtoList = null;
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    @Autowired
//    private CallUipUtil callUipUtil;
//
//    @Autowired
//    private CollectQueryService collectQueryService;
//
//    private boolean cancel = true;
//
//    @Override
//    public void run(String... args) throws Exception {
//        while (cancel){
//            loop: for(int i = 0; i < time; i ++){
//                if(IterUtil.isEmpty(dispatchDtoList))
//                {
//                    try {
//                        logger.info("尝试调用调度方法，重连尝试请求: 第{}次", i);
//                        Result result = callUipUtil.callQueryMemberByServCode(applicationName);
//                        analyzeResult(result);
//                        break loop;
//                    }catch(Exception e){
//                        logger.info("尝试调用调度方法，重连失败，进行休眠，下次调用发生在6分钟后, 异常:{}", e);
//                        sleepThread();
//                    }
//
//                }
//            }
//            this.stopThread();
//            rePublishQuery(dispatchDtoList);
//        }
//    }
//
//    public void stopThread(){
//        this.cancel = false;
//    }
//
//    public void sleepThread(){
//        try {
//            Thread.sleep(period);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void analyzeResult(Result result) throws Exception {
//        if(result.getResultCode() != ObjectToResult.SUCCESS_CODE){
//            logger.error("接口调用成功，但是返回结果失败，返回结果为: {}", result.getErrorInfo().getErrorCode());
//            return;
//        }else{
//            logger.debug("接口调用成功，已经获取返回值");
//            dispatchDtoList = JSONArray.parseArray(JSON.toJSONString(result.getData()), DispatchDto.class);
//        }
//    }
//
//    public void rePublishQuery(List<DispatchDto> dispatchDtoList){
//        if(IterUtil.isEmpty(dispatchDtoList)){
//            logger.info("调度获取的信息为空，不进行发布");
//            return;
//        }
//        List<DispatchDto> executingDispatchDto = filterDispatchDto(dispatchDtoList);
//        collectQueryService.publishedQueryList(executingDispatchDto);
//    }
//
//    public List<DispatchDto> filterDispatchDto(List<DispatchDto> dispatchDtoList){
//        logger.info("执行过滤方法，将停止任务和采集任务区分，获取到的采集任务共有 :{}条", dispatchDtoList.size());
//        Map<String, List<DispatchDto>> dispatchMap =
//                dispatchDtoList.stream().collect(groupingBy(DispatchDto::getInstanceStatus));
//
//        List<DispatchDto> executingDispatchDto = dispatchMap.get(ColInstanceStatus.executing.getCode());
//        List<DispatchDto> suspendDispatchDto = dispatchMap.get(ColInstanceStatus.suspend.getCode());
//
//        addSuspendDispatch(suspendDispatchDto);
//        return executingDispatchDto;
//    }
//
//    public void addSuspendDispatch(List<DispatchDto> suspendDispatchDto){
//        if(IterUtil.isEmpty(suspendDispatchDto)){
//            return;
//        }
//        logger.info("执行停止任务队列入队操作, 获取的停止任务共有 ：{}条", suspendDispatchDto.size());
//        suspendDispatchDto.stream().forEach(dispatchDto -> {
//            AgreementInfo agreementInfo = new AgreementInfo(dispatchDto.getAgreementToExeDto(), dispatchDto.getColSrcInfoToExeDto());
//            List<GroupDto> groupDtoList = AgreementAnalyzeUtil.parseAgreement(agreementInfo);
//            CollectQueryService.suspendCollectGroup.put(agreementInfo.getCollectSourceId(), new DispatchInfo(agreementInfo, dispatchDto.getConnectToExeDto(), groupDtoList));
//        });
//    }
//}
//
