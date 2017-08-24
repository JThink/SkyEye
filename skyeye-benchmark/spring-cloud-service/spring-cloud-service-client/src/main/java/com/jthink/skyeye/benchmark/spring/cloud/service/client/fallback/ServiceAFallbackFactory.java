package com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.iface.ServiceA;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service a 服务降级
 * @date 2017-02-24 16:34:24
 */
public class ServiceAFallbackFactory implements FallbackFactory<ServiceA> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAFallbackFactory.class);

    @Override
    public ServiceA create(Throwable cause) {
        return new ServiceA() {
            @Override
            public String invokeA() {
                ServiceAFallbackFactory.LOGGER.info(cause.toString());
                ServiceAFallbackFactory.LOGGER.info("call the fallback A");
                return "fallback A";
            }
        };
    }
}
