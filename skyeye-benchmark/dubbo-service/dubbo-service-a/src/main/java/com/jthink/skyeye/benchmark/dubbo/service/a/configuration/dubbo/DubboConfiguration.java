package com.jthink.skyeye.benchmark.dubbo.service.a.configuration.dubbo;

import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceC;
import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceD;
import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceE;
import com.jthink.skyeye.data.dubbox.DubboService;
import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-15 15:08:57
 */
@Configuration
public class DubboConfiguration {

    @Autowired
    private DubboService dubboService;

    @Bean
    public ServiceB serviceB() {
        return this.dubboService.get(ServiceB.class);
    }

    @Bean
    public ServiceC serviceC() {
        return this.dubboService.get(ServiceC.class);
    }

    @Bean
    public ServiceD serviceD() {
        return this.dubboService.get(ServiceD.class);
    }

    @Bean
    public ServiceE serviceE() {
        return this.dubboService.get(ServiceE.class);
    }
}
