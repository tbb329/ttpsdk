package com.zlst.module.handler.thread;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.zlst.common.config.GlobalConstants;
import com.zlst.common.http.CallUipUtil;
import com.zlst.module.handler.DataOutputPrcService;
import com.zlst.module.handler.bean.DevAndPlcDto;
import com.zlst.param.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取设备与PLC关系线程
 */
public class ReCallEquipInfoThread extends Thread{

	private static final Logger log = LoggerFactory.getLogger(ReCallEquipInfoThread.class);

	private CallUipUtil callUipUtil;

	private DataOutputPrcService dataOutputPrcService;

	/** 重试周期为5分钟 */
	private static final int period = 5 * 60 * 1000;

	/** 一共进行重连尝试5次 */
	private static final int time = 5;

	/* 是否关闭线程 */
	private volatile boolean cancelled = false;

	public ReCallEquipInfoThread(CallUipUtil callUipUtil, DataOutputPrcService dataOutputPrcService) {
		super();
		this.callUipUtil = callUipUtil;
		this.dataOutputPrcService = dataOutputPrcService;
	}

	@Override
	public void run() {
		while (!cancelled){
			for(int i = 0; i < time; i++){
				if(MapUtil.isEmpty(dataOutputPrcService.getPlcRelationDevMap())) {
					try {
						log.info("ReCallEquipInfoThread, 尝试调用获取设备与PLC关系信息，尝试请求: 第{}次", i);
						Result result = callUipUtil.callSearchEqBySerCode();
						Boolean anRes = analyzeResult(result);
						if(anRes){
							log.info("获取设备与PLC数据成功, 退出循环");
							break;
						}else{
							sleepThread();
						}
					}catch(Exception e){
						log.info("ReCallEquipInfoThread, 尝试调用获取设备与PLC关系方法失败，进行休眠, 下次调用发生在5分钟后, 异常信息:{}", e);
						sleepThread();
					}
				} else {
					break;
				}
			}
			cancel();
		}
	}

	private Boolean analyzeResult(Result result){
		log.info("ReCallEquipInfoThread.analyzeResult, 调用调用获取设备与PLC关系信息接口, 返回信息:{}", JSON.toJSONString(result));
		if(result != null && result.getData() != null){
			procResToMap(JSON.parseArray(JSON.toJSONString(result.getData()), DevAndPlcDto.class));
			return true;
		}else{
			return false;
		}
	}

	// 将设备数据转成对应的map
	private void procResToMap(List<DevAndPlcDto> devAndPlcDtoList){
		devAndPlcDtoList.forEach(tempDto -> {
			dataOutputPrcService.savePlcDevRel(tempDto.getCollectSourceId() + GlobalConstants.HTTP_SEPARATOR + tempDto.getStandCode(), tempDto.getEquipmentId() + GlobalConstants.HTTP_SEPARATOR + tempDto.getEquipParamCode());
		});
	}

	private void cancel() {
		cancelled = true;
	}

	public void sleepThread(){
		try {
			Thread.sleep(period);
		} catch (InterruptedException e) {
			log.error("ReCallEquipInfoThread.sleepThread, 关闭线程失败, e:{}", e);
			Thread.currentThread().interrupt();
		}
	}
}