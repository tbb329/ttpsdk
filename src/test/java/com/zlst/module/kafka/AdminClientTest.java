package com.zlst.module.kafka;

import com.zlst.ZlstApplication;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author xiong.jie/170123
 * @create 2019-01-29 14:16
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlstApplication.class)
public class AdminClientTest {

    @Autowired
    private AdminClient adminClient;

    @Test
    public void getGroupsInfo() throws  Exception{
        ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups();
        KafkaFuture<Collection<ConsumerGroupListing>> allFutures = listConsumerGroupsResult.all();
        allFutures.get().forEach(groupList -> {
            System.err.println("groupList: " + groupList);
        });
    }

    @Test
    public void getOffsetsInfo() throws  Exception{
//        adminClient.describeConsumerGroups("");
        ListConsumerGroupOffsetsResult listRes = adminClient.listConsumerGroupOffsets("caas-actuator-wqy");
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> mapKafkaFuture = listRes.partitionsToOffsetAndMetadata();
        mapKafkaFuture.get().forEach((key, value) -> {
            System.err.println("partition: " + key +", value: " + value);
        });
    }

    @Test
    public void getTopicInfo() throws  Exception{
//        adminClient.lis
    }
}
