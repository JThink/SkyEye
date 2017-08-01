package com.jthink.skyeye.client.core.kafka.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 自定义kafka的partitioner
 * @date 2016-09-09 11:23:49
 */
public class KeyModPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int partitionNum = 0;
        try {
            partitionNum = Utils.murmur2(keyBytes);
        } catch (Exception e) {
            partitionNum = key.hashCode();
        }

        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
