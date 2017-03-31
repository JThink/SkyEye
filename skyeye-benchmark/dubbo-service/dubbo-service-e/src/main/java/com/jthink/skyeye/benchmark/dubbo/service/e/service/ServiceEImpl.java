package com.jthink.skyeye.benchmark.dubbo.service.e.service;

import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-14 16:50:05
 */
@Service
public class ServiceEImpl implements ServiceE {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEImpl.class);

    @Override
    public String invokeE() {
        LOGGER.info("调用服务E");
        return "E";
    }
}
