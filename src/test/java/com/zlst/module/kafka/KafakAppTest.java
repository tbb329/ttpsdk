package com.zlst.module.kafka;

import java.util.Date;
import java.util.Random;

import com.zlst.module.handler.bean.DevAndTopicDto;
import com.zlst.module.task.enums.DevPlcOperateType;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.zlst.ZlstApplication;
import com.zlst.module.handler.bean.DataOutputBean;
import com.zlst.module.handler.kafka.KafkaHandler;

import cn.hutool.core.collection.CollUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlstApplication.class)
public class KafakAppTest {

    @Autowired
    private KafkaTemplate kafkaTemplate;
    
    @Autowired
    private KafkaHandler kafkaHandler;
    
    @Autowired
	private AdminClient adminClient;
    

    @Test
    public void testDescTopic() throws Exception {
    	String topicName = "PLC_DEV_ADD";
    	boolean flag = kafkaHandler.isExistTopic(topicName);
    	System.err.println("testDescTopic, isExistTopic : " + flag);
    }
    
    @Test
    public void testDemo() throws Exception {
    	System.err.println("---start------------");
    	int i=0;
    	while(true){
    		 kafkaTemplate.send("topictset", "this is my first demo, i: " + i);
    	     
    		 //休眠5秒，为了使监听器有足够的时间监听到topic的数据
    		 Thread.sleep(5000);
    		 i++;
    	}
    }
    
    @Test
    public void createTopic(){
    	Boolean createTopic = kafkaHandler.createTopic("custopic2");
    	System.err.println("createTopic--->" + createTopic);
    }
    
    @Test
    public void delTopic(){
    	Boolean delTopic = kafkaHandler.deleteTopic("custopic2");
    	System.err.println("delTopic--->" + delTopic);
    }
    
    @Test
    public void transferData(){
    	DataOutputBean dataOutputBean = new DataOutputBean("2c94fe976831498e01683161b851001" + new Random().nextInt(10),"123", "120", new Date());
    	System.err.println("--->dataOutputBean: " + JSON.toJSONString(dataOutputBean));
    }

	@Test
	public void sendDevTopicMsg(){
		int i=0;
		String topicSuffixWqy = "DIS_DEV_TOPIC_actuatorwqy1";
		String topicSuffixXj = "DIS_DEV_TOPIC_actuatorxj1";
		while(true){
			DevAndTopicDto dto = new DevAndTopicDto();
			dto.setDevId("dev:"+ i);
			dto.setTopicName("test1111");
			dto.setOperateType(DevPlcOperateType.addDevPlc.getCode());

			Boolean sendFlagWqy = kafkaHandler.sendMsg(topicSuffixWqy, dto.getDevId(), JSON.toJSONString(dto));
			Boolean sendFlagXj = kafkaHandler.sendMsg(topicSuffixXj, dto.getDevId(), JSON.toJSONString(dto));

			System.err.println("sendFlag---wqy--->" + sendFlagWqy);
			System.err.println("sendFlag---xj--->" + sendFlagXj);
			i++;
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {

			}
		}
	}
}

