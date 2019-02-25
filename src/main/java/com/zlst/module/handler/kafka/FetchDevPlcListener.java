package com.zlst.module.handler.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zlst.common.config.GlobalConstants;
import com.zlst.module.handler.DataOutputPrcService;
import com.zlst.module.handler.bean.DevAndPlcDto;
import com.zlst.module.handler.bean.DevAndTopicDto;

import java.util.List;

@Component
public class FetchDevPlcListener {

    private static final Logger log= LoggerFactory.getLogger(FetchDevPlcListener.class);
    
    @Autowired
    private DataOutputPrcService dataOutputPrcService;

//    @KafkaListener(id = "proDevPlcData", topics = "PLC_DEV_TOPIC_${spring.application.name}" , groupId="${spring.application.name}")
    @KafkaListener(id = "proDevPlcData", topics = "PLC_DEV_TOPIC" , groupId="${spring.application.name}")
    public void proDevPlcData(ConsumerRecord<String, String>  record) {
    	log.info("FetchDevPlcListener.proDevPlcData receive msgData :{}", record);
        try {
            DevAndPlcDto devAndPlcDto = JSON.parseObject(record.value(), DevAndPlcDto.class);
            dataOutputPrcService.proDevPlcData(devAndPlcDto);
        } catch (Exception e) {
            log.error("FetchDevPlcListener.proDevPlcData, 出现异常:{}", e);
        }

    }
    
    //    @KafkaListener(id = "proDisTopicData", topics = "DIS_DEV_TOPIC_${spring.application.name}", groupId = "${spring.application.name}")
    @KafkaListener(id = "proDisTopicData", topics = "DIS_DEV_TOPIC", groupId = "${spring.application.name}")
    public void proDisTopicData(ConsumerRecord<String, String>  record) {
        log.info("FetchDevPlcListener.proDisTopicData receive msgData : {}", record);
        try {
            DevAndTopicDto devAndTopicDto = JSON.parseObject(record.value(), DevAndTopicDto.class);
            dataOutputPrcService.proDisTopicData(devAndTopicDto);
        } catch (Exception e) {
            log.error("FetchDevPlcListener.proDisTopicData, 出现异常: {}", e);
        }
    }
}
