package com.jthink.skyeye.benchmark.dubbo.service.d.service;

import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceD;
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
public class ServiceDImpl implements ServiceD {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDImpl.class);

    @Override
    public String invokeD() {
        LOGGER.info("调用服务D");
        return "D";
    }
}
