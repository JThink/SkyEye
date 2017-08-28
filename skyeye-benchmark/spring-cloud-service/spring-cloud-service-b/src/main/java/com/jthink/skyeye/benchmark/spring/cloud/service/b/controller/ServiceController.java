package com.jthink.skyeye.benchmark.spring.cloud.service.b.controller;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.iface.ServiceB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 测试接口
 * @date 2017-06-05 11:36:52
 */
@RestController
public class ServiceController implements ServiceB {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

    @Override
    public String invokeB() {
        LOGGER.info("run service c logic code");
        return "B";
    }
}
