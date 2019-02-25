package com.zlst.common.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zlst.module.handler.bean.DataOutputBean;

//@Component
public class DemoListener {

    private static final Logger log= LoggerFactory.getLogger(DemoListener.class);

    //声明consumerID为demo，监听topicName为topic.quick.demo的Topic
    @KafkaListener(id = "demo", topics = "custopic2")
    public void listen(String msgData) {
        DataOutputBean parseObject = JSON.parseObject(msgData, DataOutputBean.class);
        log.info("demo receive parseObject : "+parseObject);
        
    }
}
