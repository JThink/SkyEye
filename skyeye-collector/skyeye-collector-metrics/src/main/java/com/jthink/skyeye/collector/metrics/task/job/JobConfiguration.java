package com.jthink.skyeye.collector.metrics.task.job;

import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.collector.core.configuration.es.EsProperties;
import com.jthink.skyeye.collector.metrics.cache.CacheService;
import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import org.I0Itec.zkclient.ZkClient;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 责任链组装3个job
 * @date 2017-08-04 09:23:57
 */
@Configuration
public class JobConfiguration {

    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private ZkClient zkClient;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private EsProperties esProperties;
    @Autowired
    private TransportClient transportClient;

    // 以下3个bean进行责任链的生成和组装
    @Bean
    public ExceptionProcessor exceptionProcessor() {
        List<EventType> exceptionProcesses = Arrays.asList(EventType.job_execute, EventType.thirdparty_call, EventType.middleware_opt, EventType.invoke_interface);
        ExceptionProcessor exceptionProcessor = new ExceptionProcessor(exceptionProcesses);
        exceptionProcessor.setRabbitmqService(this.rabbitmqService);
        exceptionProcessor.setZkClient(this.zkClient);
        return exceptionProcessor;
    }

    @Bean
    public NameCollector nameCollector(ExceptionProcessor exceptionProcessor) {
        List<EventType> names = Arrays.asList(EventType.invoke_interface, EventType.thirdparty_call, EventType.middleware_opt);
        NameCollector nameCollector = new NameCollector(names);
        nameCollector.setNextJob(exceptionProcessor);
        nameCollector.setCacheService(this.cacheService);
        return nameCollector;
    }

    @Bean
    public Indexer indexer(NameCollector nameCollector) {
        List<EventType> indexes = Arrays.asList(EventType.job_execute, EventType.thirdparty_call, EventType.middleware_opt, EventType.invoke_interface);
        Indexer indexer = new Indexer(indexes);
        indexer.setNextJob(nameCollector);
        indexer.setEsProperties(this.esProperties);
        indexer.setTransportClient(this.transportClient);
        return indexer;
    }
}
