package com.jthink.skyeye.collector.metrics.task;

import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.dto.ApiLog;
import com.jthink.skyeye.base.dto.EventLog;
import com.jthink.skyeye.base.dto.LogDto;
import com.jthink.skyeye.collector.core.callback.KafkaOffsetCommitCallback;
import com.jthink.skyeye.collector.core.configuration.kafka.KafkaProperties;
import com.jthink.skyeye.collector.core.task.Task;
import com.jthink.skyeye.collector.metrics.task.job.Indexer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 采集任务，消费采集api name、account name、third name、第三方系统异常、任务调度异常、入新es的索引（for kibana）的消费组
 * @date 2016-11-21 15:33:55
 */
@Component
public class MetricsTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsTask.class);

    @Autowired
    private KafkaConsumer kafkaConsumerEvent;
    @Autowired
    private TransportClient transportClient;
    @Autowired
    private KafkaProperties kafkaProperties;

    public static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<TopicPartition, OffsetAndMetadata>();
    private Thread thread;

    // 创建索引业务逻辑
    @Autowired
    private Indexer indexer;

    @Override
    public void run() {
        this.doTask();
    }

    @Override
    public void doTask() {
        this.thread = Thread.currentThread();
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
        int count = 0;
        try {
            while (true) {
                ConsumerRecords<byte[], String> records = this.kafkaConsumerEvent.poll(this.kafkaProperties.getPollTimeout());
                if (!records.isEmpty()) {
                    for (ConsumerRecord<byte[], String> record : records) {
                        String value = record.value();
                        LogDto logDto = this.getLogDto(value);
                        if (logDto != null) {
                            String logValue = logDto.getMessageMax();
                            // 进行map过滤操作，找出EventType为非normal的日志
                            String type = EventLog.parseEventType(logValue).symbol();
                            if (!type.equals(EventType.normal.symbol())) {
                                // 进行逻辑处理
                                if (type.equals(EventType.invoke_interface.symbol())) {
                                    // 如果是api
                                    this.indexer.doJob(ApiLog.parseEventLog(logValue), logDto, bulkRequest);
                                } else if (!type.equals(EventType.rpc_trace.symbol())) {
                                    // 非rpc trace
                                    this.indexer.doJob(EventLog.parseEventLog(logValue), logDto, bulkRequest);
                                }
                            }
                        } else {
                            LOGGER.info("record transform error, {}", value);
                        }

                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));

                        count++;
                        if (count >= 1000) {
                            // 当达到了1000触发向kafka提交offset
                            kafkaConsumerEvent.commitAsync(currentOffsets, new KafkaOffsetCommitCallback());
                            count = 0;
                        }
                    }
                    int indexCount = bulkRequest.numberOfActions();
                    if (indexCount != 0) {
                        bulkRequest.execute().actionGet();
                        bulkRequest = transportClient.prepareBulk();
                    }
                    kafkaConsumerEvent.commitAsync(currentOffsets, new KafkaOffsetCommitCallback());
                    LOGGER.info("processed {} records, {} records indexed to es", records.count(), indexCount);
                }
            }
        } catch (WakeupException e) {
            // do not process, this is shutdown
            LOGGER.error("wakeup, start to shutdown, {}", e);
        } catch (Exception e) {
            LOGGER.error("process records error, {}", e);
        } finally {
            kafkaConsumerEvent.commitSync(currentOffsets);
            LOGGER.info("finally commit the offset");
            // 不需要主动调kafkaConsumer.close(), spring bean容器会调用
        }
    }

    @Override
    public Thread executeThread() {
        return this.thread;
    }

    /**
     * 根据line构造并返回LogDto
     * @param line
     * @return
     */
    private LogDto getLogDto(String line) {
        try {
            LogDto logDto = new LogDto(line);
            return logDto;
        } catch (Exception e) {
            return null;
        }
    }
}
