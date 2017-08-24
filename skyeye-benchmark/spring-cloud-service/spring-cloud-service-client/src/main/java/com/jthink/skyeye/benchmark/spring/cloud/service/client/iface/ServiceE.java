package com.jthink.skyeye.benchmark.spring.cloud.service.client.iface;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback.ServiceEFallbackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service e 接口定义
 * @date 2017-02-24 16:34:24
 */
@FeignClient(name = "spring-cloud-service-e", fallbackFactory = ServiceEFallbackFactory.class)
@RequestMapping("service-e")
public interface ServiceE {

    @RequestMapping(value = "invokeE", method = RequestMethod.GET)
    String invokeE();
}
