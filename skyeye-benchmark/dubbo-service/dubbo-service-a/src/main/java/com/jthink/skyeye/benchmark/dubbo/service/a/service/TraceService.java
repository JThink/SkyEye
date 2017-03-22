package com.jthink.skyeye.benchmark.dubbo.service.a.service;

import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceB;
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
 * @date 2016-12-15 15:06:53
 */
@Service
public class TraceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceService.class);

    @Autowired
    private ServiceB serviceB;

    public String trace() {
        LOGGER.info("调用服务B");
        return this.serviceB.invokeB();
    }
}
