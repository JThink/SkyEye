package com.jthink.skyeye.alarm.configuration.amqp;

import com.jthink.skyeye.alarm.listener.SyncRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rabbitmq的配置
 * @date 2017-09-29 09:16:16
 */
@Configuration("amqpListenerConfiguration")
@ConfigurationProperties(prefix = "spring.queue.rabbitmq")
@ConditionalOnClass(com.jthink.skyeye.data.rabbitmq.configuration.AmqpConfiguration.class)
public class AmqpConfiguration {

    // 最大并发处理的消费者个数
    private int maxConcurrentConsumers;
    // 初始化的并发处理的消费者个数
    private int concurrentConsumers;

    @Autowired
    private SyncRequestListener syncRequestListener;
    @Autowired
    private Queue queue;

    @Bean
    public MessageListenerAdapter messageListenerAdapter(JsonMessageConverter jsonMessageConverter) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(syncRequestListener);
        messageListenerAdapter.setDefaultListenerMethod("onMessage");
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        return messageListenerAdapter;
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(this.queue);
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(this.maxConcurrentConsumers);
        container.setConcurrentConsumers(this.concurrentConsumers);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

    public int getMaxConcurrentConsumers() {
        return maxConcurrentConsumers;
    }

    public AmqpConfiguration setMaxConcurrentConsumers(int maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
        return this;
    }

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public AmqpConfiguration setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
        return this;
    }
}
