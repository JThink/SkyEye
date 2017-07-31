package com.jthink.skyeye.client.core.kafka.logback.test;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka producer test
 * @date 2016-09-07 20:33:31
 */
public class KafkaProducerDemo {

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaProducerDemo.class);

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.88.140:9092,192.168.88.145:9092,192.168.88.148:9092");

        properties.put("retries", 0);
        // props.put("batch.size", 16384);
        properties.put("linger.ms", 0);
        properties.put("max.block.ms", 3000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("partitioner.class", "com.goodix.kafka.MyPartition");

        KafkaProducer<byte[], String> producer = new KafkaProducer<byte[], String>(properties);

        ProducerRecord<byte[], String> record = new ProducerRecord<byte[], String>("app-log-test-1", "ni shi shei".getBytes(), "ni shi shei");

        producer.send(record, new Callback() {

            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (null != exception) {
                    LOGGER.error("error, {}", exception.getMessage());
                } else {
                    LOGGER.info("The offset of the record we just sent is: " + metadata.offset());
                    LOGGER.info("The partition of the record we just sent is: " + metadata.partition());
                }
            }
        });

        try {
            Thread.sleep(1000);
            producer.close();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
