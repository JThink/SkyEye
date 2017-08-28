package com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.iface.ServiceB;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service c 服务降级
 * @date 2017-02-24 16:34:24
 */
@Component
public class ServiceBFallbackFactory implements FallbackFactory<ServiceB> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBFallbackFactory.class);

    @Override
    public ServiceB create(Throwable cause) {
        return new ServiceB() {
            @Override
            public String invokeB() {
                ServiceBFallbackFactory.LOGGER.info(cause.toString());
                ServiceBFallbackFactory.LOGGER.info("call the fallback B");
                return "fallback B";
            }
        };
    }
}
