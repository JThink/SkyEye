package com.jthink.skyeye.benchmark.dubbo.service.b.service;

import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceB;
import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ServiceBImpl implements ServiceB {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBImpl.class);

    @Autowired
    private ServiceC serviceC;

    @Override
    public String invokeB() {
        LOGGER.info("调用服务C");
        return this.serviceC.invokeC();
    }
}
