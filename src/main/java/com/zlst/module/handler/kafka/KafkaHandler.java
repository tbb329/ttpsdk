package com.zlst.module.handler.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollUtil;

@Component
public class KafkaHandler {

	@Value("${kafka.producer.numPartitions}")
	private String kafaPartitionNum;

	@Value("${kafka.producer.replicationFactor}")
	private String replicationFactorNum;

    private static final Logger log = LoggerFactory.getLogger(KafkaHandler.class);

	@Autowired
	private AdminClient adminClient;

	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

	// 创建kafka topic
	public Boolean createTopic(String topicName){
		log.info("KafkaHandler.createTopic, topicName:{}", topicName);
        try {

        	Boolean existFlag = isExistTopic(topicName);
        	log.info("KafkaHandler.createTopic, existFlag:{}", existFlag);
        	if(existFlag){
        		return true;
        	}

        	NewTopic topic = new NewTopic(topicName, Integer.parseInt(kafaPartitionNum) , Short.parseShort(replicationFactorNum));
        	adminClient.createTopics(CollUtil.toList(topic));
			Thread.sleep(500);
			return true;
		} catch (Exception e) {
			log.error("KafkaHandler.createTopic, topicName:{}, 创建出现异常:{}", topicName, e);
			return false;
		}
	}

	// 删除kafka topic
	public Boolean deleteTopic(String topicName){
		log.info("KafkaHandler.deleteTopic, topicName:{}", topicName);
        try {
        	Boolean existFlag = isExistTopic(topicName);
        	log.info("KafkaHandler.deleteTopic, existFlag:{}", existFlag);
        	if(!existFlag){
        		return true;
        	}

        	adminClient.deleteTopics(CollUtil.toList(topicName));
			Thread.sleep(500);
			return true;
		} catch (Exception e) {
			log.error("KafkaHandler.deleteTopic, topicName:{}, 删除出现异常:{}", topicName, e);
			return false;
		}
	}

	public Boolean isExistTopic(String topicName) {
        try {
            ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
            listTopicsOptions.listInternal(true);
            ListTopicsResult res = adminClient.listTopics(listTopicsOptions);
           	return res.names().get().contains(topicName);
        } catch (Exception e) {
         	log.error("KafkaHandler.isExistTopic, topicName:{}, 查询出现异常:{}", topicName, e);
            return false;
        }
    }

	// 发送数据
	public Boolean sendMsg(String topicName,String key, String msg){
		log.debug("KafkaHandler.sendMsg, topicName:{}, msg:{}", topicName, msg);
		try {
			// 异步发送
			kafkaTemplate.send(topicName, key, msg);
			return true;
		} catch (Exception e) {
			log.error("KafkaHandler.sendMsg, topicName:{}, key:{}, msg:{}, 发送出现异常:{}", topicName, key, msg, e);
			return false;
		}
	}

}
