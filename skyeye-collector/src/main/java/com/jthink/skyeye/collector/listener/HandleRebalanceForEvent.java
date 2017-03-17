package com.jthink.skyeye.collector.listener;

import com.jthink.skyeye.collector.configuration.kafka.KafkaProperties;
import com.jthink.skyeye.collector.task.CollectTask;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rebalance回调
 * @date 2016-09-20 11:14:27
 */
@Component
public class HandleRebalanceForEvent implements ConsumerRebalanceListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleRebalanceForEvent.class);

    @Autowired
    private KafkaConsumer kafkaConsumerEvent;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        this.kafkaConsumerEvent.commitSync(CollectTask.currentOffsets);
        LOGGER.info("before rebalance, commit offset once");
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.kafkaConsumerEvent.subscribe(Arrays.asList(this.kafkaProperties.getTopic()), this);
    }
}