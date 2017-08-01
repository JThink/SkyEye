package com.jthink.skyeye.client.core.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc double check实现KafkaProducer的懒加载
 * @date 2016-09-09 09:02:34
 */
public class LazySingletonProducer {

    private static volatile Producer<byte[], String> producer;

    /**
     * 私有化构造方法
     */
    private LazySingletonProducer() {

    }

    /**
     * 实例化
     * @param config
     * @return
     */
    public static Producer<byte[], String> getInstance(Map<String, Object> config) {
        if (producer == null) {
            synchronized(LazySingletonProducer.class) {
                if (producer == null) {
                    producer = new KafkaProducer<byte[], String>(config);
                }
            }
        }
        return producer;
    }

    /**
     * 是否初始化
     * @return
     */
    public static boolean isInstanced() {
        return producer != null;
    }
}
