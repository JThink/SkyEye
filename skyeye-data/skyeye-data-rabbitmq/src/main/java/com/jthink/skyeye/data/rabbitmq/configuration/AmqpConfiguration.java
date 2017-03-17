package com.jthink.skyeye.data.rabbitmq.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rabbitmq的配置
 * @date 2016-11-22 09:16:16
 */
@Configuration
@ConfigurationProperties(prefix = "spring.queue.rabbitmq")
public class AmqpConfiguration {

    // 队列名字
    private String queue;
    // exchange
    private String exchange;
    // routingKey
    private String routingKey;

    @Bean
    public Queue queue() {
        return new Queue(this.queue, true);
    }

    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(this.exchange);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(this.routingKey);
    }

    @Bean
    public JsonMessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setQueue(this.queue);
        rabbitTemplate.setExchange(this.exchange);
        rabbitTemplate.setRoutingKey(this.routingKey);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
