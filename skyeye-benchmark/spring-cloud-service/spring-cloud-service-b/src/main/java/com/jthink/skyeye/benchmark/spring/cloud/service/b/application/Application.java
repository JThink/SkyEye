package com.jthink.skyeye.benchmark.spring.cloud.service.b.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**y
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2017-08-27 10:10:57
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.jthink.skyeye.benchmark.spring.cloud.service.b", "com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback"})
@PropertySource("classpath:properties/service-b.properties")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).run(args);
        LOGGER.info("spring cloud service b start successfully");
    }
}
