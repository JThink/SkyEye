package com.jthink.skyeye.data.dubbox;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc dubbo auto configuration
 * @date 2016-12-15 15:08:57
 */
@Configuration
@EnableConfigurationProperties(DubboProperties.class)
@ConditionalOnClass(DubboService.class)
public class DubboAutoConfiguration {

    @Autowired
    private DubboProperties dubboProperties;

    @Bean
    public ReferenceConfigCache referenceConfigCache() {
        return ReferenceConfigCache.getCache();
    }

    @Bean
    public ApplicationConfig applicationConfig() {
        return new ApplicationConfig(this.dubboProperties.getName());
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig(this.dubboProperties.getAddress());
        registryConfig.setCheck(this.dubboProperties.isCheck());
        registryConfig.setRegister(this.dubboProperties.isRegister());
        registryConfig.setProtocol(this.dubboProperties.getProtocol());

        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        if (null == this.dubboProperties.getProtocolName() || null == this.dubboProperties.getPayload()) {
            return null;
        }
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(-1);
        protocolConfig.setPayload(this.dubboProperties.getPayload());
        return protocolConfig;
    }

    @Bean
    public DubboService dubboService(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig, ReferenceConfigCache referenceConfigCache) {
        if (null == protocolConfig) {
            return new DubboService(applicationConfig, registryConfig, this.dubboProperties, referenceConfigCache);
        }
        return new DubboService(applicationConfig, registryConfig, protocolConfig, this.dubboProperties, referenceConfigCache);
    }

}
