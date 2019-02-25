package com.zlst.module.handler.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaSendResultListener implements ProducerListener<String, String> {

    private static final Logger log = LoggerFactory.getLogger(KafkaSendResultListener.class);

    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
        log.info("Message send success : " + producerRecord.toString());
    }

    @Override
    public void onError(ProducerRecord<String, String> producerRecord, Exception exception) {
        log.info("Message send error : " + producerRecord.toString());
    }
}

