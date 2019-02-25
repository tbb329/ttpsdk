package com.zlst.module.handler.kafka;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomPartitioner implements Partitioner {
	
	private static final Logger log = LoggerFactory.getLogger(CustomPartitioner.class);
	
    public void configure(Map<String, ?> configs) {
    	
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    	List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
		int numPartitions = partitions.size();
		int res = 1;
		res = Math.abs(key.hashCode()) % numPartitions;
		log.debug("CustomPartitioner, partition, key:{}, value:{}, numPartitions:{}, res:{}", key, value, numPartitions, res);
        return res;
    }

    public void close() {
    	
    }
}
