package com.jthink.skyeye.collector.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 项目启动器
 * @date 2016-08-24 18:31:48
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"com.jthink.skyeye.collector", "com.jthink.skyeye.data.jpa", "com.jthink.skyeye.data.rabbitmq"})
@PropertySource("file:/opt/jthink/jthink-config/skyeye/collector/collector.properties")
//@PropertySource("classpath:properties/collector.properties")
public class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Launcher.class);
        Set<ApplicationListener<?>> listeners = builder.application().getListeners();
        for (Iterator<ApplicationListener<?>> it = listeners.iterator(); it.hasNext();) {
            ApplicationListener<?> listener = it.next();
            if (listener instanceof LoggingApplicationListener) {
                it.remove();
            }
        }
        builder.application().setListeners(listeners);
        ConfigurableApplicationContext context = builder.run(args);
        LOGGER.info("collector start successfully");

        KafkaConsumer kafkaConsumerApp = (KafkaConsumer<byte[], String>) context.getBean("kafkaConsumerApp");
        KafkaConsumer kafkaConsumerEvent = (KafkaConsumer<byte[], String>) context.getBean("kafkaConsumerEvent");
        KafkaConsumer kafkaConsumerBackup = (KafkaConsumer<byte[], String>) context.getBean("kafkaConsumerBackup");
        KafkaConsumer kafkaConsumerRpcTrace = (KafkaConsumer<byte[], String>) context.getBean("kafkaConsumerRpcTrace");
        TaskExecutor taskExecutor = context.getBean(TaskExecutor.class);

        // 优雅停止项目
        Runtime.getRuntime().addShutdownHook(new ShutdownHookRunner(Arrays.asList(kafkaConsumerApp, kafkaConsumerEvent,
                kafkaConsumerBackup, kafkaConsumerRpcTrace), taskExecutor));

        taskExecutor.addTask();
        taskExecutor.execute();
    }

    private static class ShutdownHookRunner extends Thread {

        private List<KafkaConsumer> kafkaConsumers;
        private TaskExecutor taskExecutor;

        public ShutdownHookRunner(List<KafkaConsumer> kafkaConsumers, TaskExecutor taskExecutor) {
            this.kafkaConsumers = kafkaConsumers;
            this.taskExecutor = taskExecutor;
        }

        @Override
        public void run() {
            LOGGER.info("starting to exit");
            for (KafkaConsumer kafkaConsumer : kafkaConsumers) {
                kafkaConsumer.wakeup();
            }
            try {
                taskExecutor.join();
            } catch (InterruptedException e) {
                LOGGER.error("interrupted, ", e);
            }
        }
    }
}
