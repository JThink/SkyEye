package com.jthink.skyeye.benchmark.dubbo.service.e.configuration.dubbo;

import com.jthink.skyeye.data.dubbo.DubboService;
import com.jthink.skyeye.benchmark.dubbo.service.client.ServiceE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc dubbo的配置
 * @date 2016-12-14 16:58:18
 */
@Configuration
public class DubboConfiguration {

    @Autowired
    private DubboService dubboService;
    @Autowired
    private ServiceE serviceEImpl;

    @PostConstruct
    public void exportDubboService() {
        this.dubboService.export(ServiceE.class, this.serviceEImpl);
    }
}
