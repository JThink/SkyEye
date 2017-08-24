package com.jthink.skyeye.benchmark.spring.cloud.service.client.iface;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback.ServiceBFallbackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service b 接口定义
 * @date 2017-02-24 16:34:24
 */
@FeignClient(name = "spring-cloud-service-b", fallbackFactory = ServiceBFallbackFactory.class)
@RequestMapping("service-b")
public interface ServiceB {

    @RequestMapping(value = "invokeB", method = RequestMethod.GET)
    String invokeB();
}
