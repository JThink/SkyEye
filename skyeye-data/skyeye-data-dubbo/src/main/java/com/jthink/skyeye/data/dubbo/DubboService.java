package com.jthink.skyeye.data.dubbo;

import com.alibaba.dubbo.config.*;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 提供暴露和获取service的方法
 * @date 2016-12-15 14:23:24
 */
public class DubboService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboService.class);

    private ApplicationConfig applicationConfig;
    private RegistryConfig registryConfig;
    private ProtocolConfig protocolConfig;
    private DubboProperties dubboProperties;
    private ReferenceConfigCache referenceConfigCache;
    private static Map<Class<?>, ReferenceConfig<?>> referenceConfigMap = new ConcurrentHashMap<Class<?>, ReferenceConfig<?>>();

    public DubboService(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ProtocolConfig protocolConfig, DubboProperties dubboProperties, ReferenceConfigCache referenceConfigCache) {
        this.applicationConfig = applicationConfig;
        this.registryConfig = registryConfig;
        this.protocolConfig = protocolConfig;
        this.dubboProperties = dubboProperties;
        this.referenceConfigCache = referenceConfigCache;
    }

    public DubboService(ApplicationConfig applicationConfig, RegistryConfig registryConfig, DubboProperties dubboProperties, ReferenceConfigCache referenceConfigCache) {
        this.applicationConfig = applicationConfig;
        this.registryConfig = registryConfig;
        this.dubboProperties = dubboProperties;
        this.referenceConfigCache = referenceConfigCache;
    }

    /**
     * 将服务暴露出去
     * @param service
     * @param serviceImpl
     * @param <T>
     */
    public <T> void export(Class<?> service, T serviceImpl) {
        ServiceConfig<T> serviceConfig = new ServiceConfig<T>();

        serviceConfig.setInterface(service);
        serviceConfig.setRef(serviceImpl);

        serviceConfig.setApplication(this.applicationConfig);
        serviceConfig.setRegistry(this.registryConfig);
        serviceConfig.setProtocol(this.protocolConfig);
        serviceConfig.setTimeout(this.dubboProperties.getTimeOut());
        serviceConfig.setRetries(this.dubboProperties.getRetries());

        serviceConfig.export();
    }

    /**
     * 获取dubbo暴露的service
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        ReferenceConfig<?> referenceConfig = referenceConfigMap.get(clazz);

        if (null == referenceConfig) {
            synchronized (clazz) {
                referenceConfig = referenceConfigMap.get(clazz);
                if (null == referenceConfig) {
                    referenceConfig = getReferenceConfig(clazz);
                    referenceConfigMap.put(clazz, referenceConfig);
                }
            }
        }

        T t = null;
        try{
            t = (T) referenceConfigCache.get(referenceConfig);
        } catch (Exception e) {
            LOGGER.info("dubbo 获取远程服务异常", e);
        }
        return t;
    }

    private <T> ReferenceConfig<T> getReferenceConfig(Class<T> clazz) {
        ReferenceConfig<T> reference = new ReferenceConfig<T>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setRetries(dubboProperties.getRetries());
        reference.setInterface(clazz);
        reference.setTimeout(dubboProperties.getTimeOut());
        return reference;
    }
}
