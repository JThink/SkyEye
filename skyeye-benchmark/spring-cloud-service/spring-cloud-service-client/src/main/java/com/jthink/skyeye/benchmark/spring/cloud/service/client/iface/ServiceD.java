package com.jthink.skyeye.benchmark.spring.cloud.service.client.iface;

import com.jthink.skyeye.benchmark.spring.cloud.service.client.fallback.ServiceDFallbackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc service d 接口定义
 * @date 2017-02-24 16:34:24
 */
@FeignClient(name = "spring-cloud-service-d", fallbackFactory = ServiceDFallbackFactory.class)
@RequestMapping("service-d")
public interface ServiceD {

    @RequestMapping(value = "invokeD", method = RequestMethod.GET)
    String invokeD();
}
