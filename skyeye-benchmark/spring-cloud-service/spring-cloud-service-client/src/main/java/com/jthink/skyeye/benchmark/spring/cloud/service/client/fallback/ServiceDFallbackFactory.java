package com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.iface.ServiceD;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service D 服务降级
 * @date 2017-02-24 16:34:24
 */
@Component
public class ServiceDFallbackFactory implements FallbackFactory<ServiceD> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDFallbackFactory.class);

    @Override
    public ServiceD create(Throwable cause) {
        return new ServiceD() {
            @Override
            public String invokeD() {
                ServiceDFallbackFactory.LOGGER.info(cause.toString());
                ServiceDFallbackFactory.LOGGER.info("call the fallback D");
                return "fallback D";
            }
        };
    }
}
