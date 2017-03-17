package com.jthink.skyeye.collector.configuration.kafka;

import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import com.jthink.skyeye.collector.configuration.es.EsProperties;
import com.jthink.skyeye.collector.service.CacheService;
import com.jthink.skyeye.collector.task.job.ExceptionProcessor;
import com.jthink.skyeye.collector.task.job.Indexer;
import com.jthink.skyeye.collector.task.job.NameCollector;
import com.jthink.skyeye.base.constant.EventType;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka配置
 * @date 2016-09-18 11:12:07
 */
@Configuration
@EnableConfigurationProperties({KafkaProperties.class, EsProperties.class})
public class KafkaConfiguration {

    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private TransportClient transportClient;
    @Autowired
    private EsProperties esProperties;
    @Autowired
    private ZkClient zkClient;

    // 消费入es的消费组
    @Bean(name = "kafkaConsumerApp")
    public KafkaConsumer<byte[], String> kafkaConsumerApp() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBrokers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaProperties.getIndexerGroup());
        // 手动commit offset到kafka(该版本不将offset保存到zk)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<byte[], String> kafkaConsumer = new KafkaConsumer<byte[], String>(config);

        return kafkaConsumer;
    }

    // 消费采集api name、account name、third name、第三方系统异常、任务调度异常、入新es的索引（for kibana）的消费组
    @Bean(name = "kafkaConsumerEvent")
    public KafkaConsumer<byte[], String> kafkaConsumerEvent() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBrokers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaProperties.getCollectGroup());
        // 手动commit offset到kafka(该版本不将offset保存到zk)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<byte[], String> kafkaConsumer = new KafkaConsumer<byte[], String>(config);

        return kafkaConsumer;
    }

    // 消费入hdfs备份的消费组
    @Bean(name = "kafkaConsumerBackup")
    public KafkaConsumer<byte[], String> kafkaConsumerBackup() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBrokers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaProperties.getBackupGroup());
        // 手动commit offset到kafka(该版本不将offset保存到zk)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<byte[], String> kafkaConsumer = new KafkaConsumer<byte[], String>(config);

        return kafkaConsumer;
    }

    // rpc trace跟踪入库的消费组
    @Bean(name = "kafkaConsumerRpcTrace")
    public KafkaConsumer<byte[], String> kafkaConsumerRpcTrace() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBrokers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, this.kafkaProperties.getRpcTraceGroup());
        // 手动commit offset到kafka(该版本不将offset保存到zk)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<byte[], String> kafkaConsumer = new KafkaConsumer<byte[], String>(config);

        return kafkaConsumer;
    }

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
        List<EventType> names = Arrays.asList(EventType.invoke_interface, EventType.thirdparty_call);
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
