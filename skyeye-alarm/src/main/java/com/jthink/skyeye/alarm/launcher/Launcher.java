package com.jthink.skyeye.alarm.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.util.Iterator;
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
@ComponentScan(basePackages = {"com.jthink.skyeye.alarm", "com.jthink.skyeye.data.rabbitmq"})
@PropertySource("file:/opt/jthink/jthink-config/skyeye/alarm/alarm.properties")
//@PropertySource("classpath:properties/alarm.properties")
public class Launcher {

    private static volatile boolean RUNNING = true;
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
        builder.run(args);
        LOGGER.info("alarm start successfully");

        // 优雅停止项目
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.info("try to stop the system");
                synchronized (Launcher.class) {
                    RUNNING = false;
                    Launcher.class.notify();
                }
            }
        });

        synchronized (Launcher.class) {
            while (RUNNING) {
                try {
                    Launcher.class.wait();
                } catch (InterruptedException e) {
                    LOGGER.error("wait error");
                    e.printStackTrace();
                }
            }
        }
    }
}
