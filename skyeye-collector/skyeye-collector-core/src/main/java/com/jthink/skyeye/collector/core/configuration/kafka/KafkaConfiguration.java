package com.jthink.skyeye.collector.core.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka配置
 * @date 2016-09-18 11:12:07
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;

    // kafka consumer
    @Bean
    public KafkaConsumer<byte[], String> kafkaConsumer() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBrokers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaProperties.getConsumeGroup());
        // 手动commit offset到kafka(该版本不将offset保存到zk)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<byte[], String> kafkaConsumer = new KafkaConsumer<byte[], String>(config);

        return kafkaConsumer;
    }

}
