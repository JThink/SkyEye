package com.jthink.skyeye.client.core.kafka.partition.test;

import org.apache.kafka.common.utils.Utils;

import java.nio.ByteBuffer;


/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-11-02 14:38:47
 */
public class PartitionTest {

    public static void main(String[] args) {
        String app = "app-test1";
        String host = "jthink";
        int numPartitions = 9;

        String key = new StringBuilder(app).append(host).toString();
        byte[] keyBytes= ByteBuffer.allocate(4).putInt(key.hashCode()).array();
        int partitionNum = 0;
        try {
            partitionNum = Utils.murmur2(keyBytes);
        } catch (Exception e) {
            partitionNum = key.hashCode();
        }
        System.out.println(Math.abs(partitionNum % numPartitions));
    }
}
