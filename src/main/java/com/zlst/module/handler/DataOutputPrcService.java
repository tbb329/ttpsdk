package com.zlst.module.handler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.zlst.module.handler.cache.DevRealDataCache;
import com.zlst.module.handler.thread.ReCallDevTopicInfoThread;
import com.zlst.module.handler.thread.ReCallEquipInfoThread;
import com.zlst.module.task.enums.DevPlcOperateType;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zlst.common.config.GlobalConstants;
import com.zlst.common.http.CallUipUtil;
import com.zlst.module.handler.bean.DataOutputBean;
import com.zlst.module.handler.bean.DevAndPlcDto;
import com.zlst.module.handler.bean.DevAndTopicDto;
import com.zlst.module.handler.bean.PlcToDevDto;
import com.zlst.module.handler.kafka.KafkaHandler;
import com.zlst.module.task.enums.TopicOperateType;
import com.zlst.param.Result;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 采集数据转换服务
 * @author xiong.jie/170123
 * @create 2019年1月10日
 */
@Component
public class DataOutputPrcService {

	private static final Logger log = LoggerFactory.getLogger(DataOutputPrcService.class);

	private static ConcurrentHashMap<String, String> plcRelationDevMap = new ConcurrentHashMap<String, String>();

	// 设备与TOPIC映射关系
	private static ConcurrentHashMap<String, Set<String>> devAndTopicMap = new ConcurrentHashMap<String, Set<String>>();

	@Autowired
	private CallUipUtil callUipUtil;

	@Autowired
	private KafkaHandler kafkaHandler;

	@Autowired
	private DevRealDataCache devRealDataCache;

    @PostConstruct
    public void reCallEquInfoThread(){
    	log.info("服务启动或重启, 执行获取设备与PLC关系信息");
    	ReCallEquipInfoThread reCallEquipInfoThread = new ReCallEquipInfoThread(callUipUtil, this);
    	reCallEquipInfoThread.start();

		log.info("服务启动或重启, 执行获取设备与TOPIC关系信息");
		ReCallDevTopicInfoThread reCallDevTopicInfoThread = new ReCallDevTopicInfoThread(callUipUtil, this);
		reCallDevTopicInfoThread.start();
    }

    // 保存plc与设备对应关系, plcKey->采集源id:数据编码, devValue->设备id:属性
	public void savePlcDevRel(String plcKey, String devValue){
		plcRelationDevMap.put(plcKey, devValue);
		log.info("DataOutputPrcService.savePlcDevRel success, plcRelationDevMap size:{}", plcRelationDevMap.size());
	}

	public void delPlcDevRel(String plcKey){
		plcRelationDevMap.remove(plcKey);
		log.info("DataOutputPrcService.delPlcDevRel success, plcRelationDevMap size:{}", plcRelationDevMap.size());
	}

	// 保存设备与topic之间关系
	public void saveDevTopicRel(String devId, String topicName){
		Set<String> topicSet = devAndTopicMap.get(devId);
		if(CollUtil.isEmpty(topicSet)){
			topicSet = new HashSet<>();
		}
		topicSet.add(topicName);
		devAndTopicMap.put(devId, topicSet);
		log.info("DataOutputPrcService.saveDevTopicRel success, devId:{}, devAndTopicMap size:{}, topicSet:{}", devId, devAndTopicMap.size(), topicSet);
	}

	// 删除设备对应TOPIC关系
	public void delDevTopicRel(String devId, String topicName){
		Set<String> topicSet = devAndTopicMap.get(devId);
		if(CollUtil.isNotEmpty(topicSet)){
			topicSet.remove(topicName);
			log.info("DataOutputPrcService.delDevTopicRel success, devAndTopicMap size:{}", devAndTopicMap.size());
		}else{
			log.error("DataOutputPrcService.delDevTopicRel fail, devId:{}, topicName:{} 对应的信息不存在", devId, topicName);
		}
	}

	public void proDisTopicData(DevAndTopicDto devAndTopicDto){
		if(TopicOperateType.addTopic.getCode().equals(devAndTopicDto.getOperateType())){

			kafkaHandler.createTopic(devAndTopicDto.getTopicName());

		} else if(TopicOperateType.delTopic.getCode().equals(devAndTopicDto.getOperateType())){

			kafkaHandler.deleteTopic(devAndTopicDto.getTopicName());

		} else if(TopicOperateType.addTopicDev.getCode().equals(devAndTopicDto.getOperateType())){

			saveDevTopicRel(devAndTopicDto.getDevId(), devAndTopicDto.getTopicName());

		} else if(TopicOperateType.delTopicDev.getCode().equals(devAndTopicDto.getOperateType())){

			delDevTopicRel(devAndTopicDto.getDevId(), devAndTopicDto.getTopicName());
		}
	}

	public void proDevPlcData(DevAndPlcDto devAndPlcDto){
		if(DevPlcOperateType.addDevPlc.getCode().equals(devAndPlcDto.getOperateType())){
			savePlcDevRel(devAndPlcDto.getCollectSourceId() + GlobalConstants.HTTP_SEPARATOR + devAndPlcDto.getStandCode(), devAndPlcDto.getEquipmentId() + GlobalConstants.HTTP_SEPARATOR + devAndPlcDto.getEquipParamCode());

		} else if(TopicOperateType.delTopic.getCode().equals(devAndPlcDto.getOperateType())){
			delPlcDevRel(devAndPlcDto.getCollectSourceId() + GlobalConstants.HTTP_SEPARATOR + devAndPlcDto.getStandCode());
		}
	}

		// 将PLC数据转换为设备数据, 并发送到指定topic
	public void processData(List<PlcToDevDto> plcToDevDtos){
		if(CollUtil.isEmpty(plcToDevDtos)){
			log.info("DataOutputPrcService.processData, plcToDevDtos is null");
			return;
		}

		plcToDevDtos.forEach(tempDto -> {
			String devValue = plcRelationDevMap.get(tempDto.getColSrcId() + GlobalConstants.HTTP_SEPARATOR + tempDto.getDataCode());
			if(StrUtil.isEmpty(devValue)){
				log.info("DataOutputPrcService.processData, colSrcId:{}, dataCode:{}, 对应的设备信息为空", tempDto.getColSrcId(), tempDto.getDataCode());
			}else{
				String[] devArray = devValue.split(GlobalConstants.HTTP_SEPARATOR);
				DataOutputBean dataOutputBean = new DataOutputBean(devArray[0], devArray[1], tempDto.getDataValue(), new Date());
				sendMsg(dataOutputBean);
				//缓存实时数据
				devRealDataCache.cacheRealData(dataOutputBean);
			}
		});

	}

	// 根据devId查找对应的topicName,并发送
	public void sendMsg(DataOutputBean dataOutputBean){
		String devId = dataOutputBean.getDevId();
		Set<String> topicSet = devAndTopicMap.get(devId);

		if(CollUtil.isNotEmpty(topicSet)){
			for(String topicName : topicSet){
				kafkaHandler.sendMsg(topicName, devId, JSON.toJSONString(dataOutputBean));
			}
		}else {
			log.debug("DataOutputPrcService.devId:{} 对应topicaName为空, 请检查配置", devId);
		}
	}

	public  ConcurrentHashMap<String, String> getPlcRelationDevMap() {
		return plcRelationDevMap;
	}

	public  ConcurrentHashMap<String, Set<String>> getDevAndTopicMap() {
		return devAndTopicMap;
	}
}


