package com.jthink.skyeye.collector.trace.launcher;

import com.jthink.skyeye.collector.core.hook.ShutdownHookRunner;
import com.jthink.skyeye.collector.core.task.Task;
import org.apache.kafka.clients.consumer.KafkaConsumer;
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

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

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
@ComponentScan(basePackages={"com.jthink.skyeye.collector.core", "com.jthink.skyeye.collector.trace", "com.jthink.skyeye.data.jpa"})
@PropertySource("file:/opt/jthink/jthink-config/skyeye/collector/collector-trace.properties")
//@PropertySource("classpath:properties/collector-trace.properties")
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
        LOGGER.info("collector trace start successfully");

        KafkaConsumer kafkaConsumer = (KafkaConsumer<byte[], String>) context.getBean("kafkaConsumer");
        Task task = (Task) context.getBean("rpcTraceTask");

        // 优雅停止项目
        Runtime.getRuntime().addShutdownHook(new ShutdownHookRunner(kafkaConsumer, task));
        task.doTask();
    }

}
