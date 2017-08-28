package com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.iface.ServiceE;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service e 服务降级
 * @date 2017-02-24 16:34:24
 */
@Component
public class ServiceEFallbackFactory implements FallbackFactory<ServiceE> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEFallbackFactory.class);

    @Override
    public ServiceE create(Throwable cause) {
        return new ServiceE() {
            @Override
            public String invokeE() {
                ServiceEFallbackFactory.LOGGER.info(cause.toString());
                ServiceEFallbackFactory.LOGGER.info("call the fallback E");
                return "fallback E";
            }
        };
    }
}
