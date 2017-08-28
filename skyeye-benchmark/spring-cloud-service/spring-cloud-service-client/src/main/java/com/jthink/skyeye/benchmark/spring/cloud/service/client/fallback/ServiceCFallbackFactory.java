package com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.iface.ServiceC;
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
public class ServiceCFallbackFactory implements FallbackFactory<ServiceC> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCFallbackFactory.class);

    @Override
    public ServiceC create(Throwable cause) {
        return new ServiceC() {
            @Override
            public String invokeC() {
                ServiceCFallbackFactory.LOGGER.info(cause.toString());
                ServiceCFallbackFactory.LOGGER.info("call the fallback C");
                return "fallback C";
            }
        };
    }
}
