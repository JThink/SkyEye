package com.jthink.skyeye.collector.trace.cache;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.NameInfoType;
import com.jthink.skyeye.data.jpa.domain.NameInfo;
import com.jthink.skyeye.data.jpa.domain.ServiceInfo;
import com.jthink.skyeye.data.jpa.repository.NameInfoRepository;
import com.jthink.skyeye.data.jpa.repository.ServiceInfoRepository;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 缓存名称采集相关的数据
 * @date 2016-11-22 09:23:19
 */
@Service
public class CacheService implements InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private ServiceInfoRepository serviceInfoRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;
    private SetOperations<String, String> setOps;

    private static final String CONFIG_PREFIX = "jthink_monitor_collector";
    private static final String SERVICE_INFO_PREFIX = "jthink_monitor_collector_service_info";
    public static final String SERVICE_INFO_TYPE = "service";

    private static final Map<String, String> mapping = new HashMap<String, String>() {
        {
            put(SERVICE_INFO_TYPE, SERVICE_INFO_PREFIX);
        }
    };

    /**
     * 保存
     * @param serviceInfo
     */
    public void save(ServiceInfo serviceInfo) {
        this.serviceInfoRepository.save(serviceInfo);
    }

    /**
     * 根据采集的类型和值存入redis
     * @param type
     * @param value
     * @return
     */
    public void add(String type, String value) {
        this.setOps.add(mapping.get(type), value);
    }

    /**
     * 根据采集的类型和值判断是否存在
     * @param type
     * @param value
     * @return
     */
    public boolean isExists(String type, String value) {
        return this.setOps.isMember(mapping.get(type), value);
    }

    /**
     * 将数据库中的配置表进行缓存
     */
    private void loadCache() {
        StopWatch sw = new StopWatch();
        sw.start();
        LOGGER.info("start load config to cache");

        Iterable<ServiceInfo> serviceInfos = this.serviceInfoRepository.findAll();

        for (Iterator<ServiceInfo> it = serviceInfos.iterator(); it.hasNext();) {
            ServiceInfo serviceInfo = it.next();
            this.setOps.add(SERVICE_INFO_PREFIX, serviceInfo.getSid());
        }

        sw.stop();
        LOGGER.info("load config to cache end, cost {} ms", sw.getTime());
    }

    /**
     * 将redis中的配置信息清除
     */
    private void clearCache() {
        StopWatch sw = new StopWatch();
        sw.start();
        LOGGER.info("start clear config cache");

        Set<String> keys = this.redisTemplate.keys(CONFIG_PREFIX + Constants.XING_HAO);
        this.redisTemplate.delete(keys);

        sw.stop();
        LOGGER.info("clear config cache end, cost {} ms", sw.getTime());
    }

    /**
     * 缓存数据初始化
     */
    public void load() {
        this.clearCache();
        this.loadCache();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setOps = this.redisTemplate.opsForSet();
        this.load();
    }
}
