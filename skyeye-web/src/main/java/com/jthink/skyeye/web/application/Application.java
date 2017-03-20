package com.jthink.skyeye.web.application;

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
 * @desc 项目启动
 * @date 2016-10-08 10:29:33
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.jthink.skyeye.web", "com.jthink.skyeye.data.jpa", "com.jthink.skyeye.data.rabbitmq"})
@PropertySource("file:/opt/jthink/jthink-config/skyeye/web/web.properties")
//@PropertySource("classpath:properties/web.properties")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
        Set<ApplicationListener<?>> listeners = builder.application().getListeners();
        for (Iterator<ApplicationListener<?>> it = listeners.iterator(); it.hasNext();) {
            ApplicationListener<?> listener = it.next();
            if (listener instanceof LoggingApplicationListener) {
                it.remove();
            }
        }
        builder.application().setListeners(listeners);
        builder.run(args);

        LOGGER.info("web start successfully");
    }
}
