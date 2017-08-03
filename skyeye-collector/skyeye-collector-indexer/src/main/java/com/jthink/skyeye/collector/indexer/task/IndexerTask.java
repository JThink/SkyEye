package com.jthink.skyeye.collector.indexer.task;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.dto.LogDto;
import com.jthink.skyeye.collector.core.callback.KafkaOffsetCommitCallback;
import com.jthink.skyeye.collector.core.configuration.es.EsProperties;
import com.jthink.skyeye.collector.core.configuration.kafka.KafkaProperties;
import com.jthink.skyeye.collector.core.task.Task;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka2es 建立index的task
 * @date 2016-09-20 10:25:13
 */
@Component
public class IndexerTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerTask.class);

    @Autowired
    private KafkaConsumer kafkaConsumer;
    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private TransportClient transportClient;
    @Autowired
    private EsProperties esProperties;

    public static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<TopicPartition, OffsetAndMetadata>();
    private Thread thread;

    @Override
    public void run() {
        this.doTask();
    }

    /**
     * 任务执行, 该消费kafka的策略有极小的可能性会丢失或者重复消费, 使用的是手动提交offset的方法, 确保不丢数据和重复消费需要将索引存储到第三方存储中, 并且需要写回滚机制
     * 该方式在程序意外退出的情况下有可能会丢失到es的数据
     */
    @Override
    public void doTask() {
        this.thread = Thread.currentThread();
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();
        int count = 0;
        try {
            while (true) {
                ConsumerRecords<byte[], String> records = this.kafkaConsumer.poll(this.kafkaProperties.getPollTimeout());
                if (!records.isEmpty()) {
                    for (ConsumerRecord<byte[], String> record : records) {
                        String value = record.value();
                        XContentBuilder source = this.buildXContentBuilder(value);
                        if (source != null) {
                            bulkRequest.add(transportClient.prepareIndex(this.esProperties.getIndex(), this.esProperties.getDoc())
                                    .setSource(source));
                        } else {
                            LOGGER.info("record transform error, {}", value);
                        }
                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));

                        count++;
                        if (count >= 1000) {
                            // 当达到了1000触发向kafka提交offset
                            kafkaConsumer.commitAsync(currentOffsets, new KafkaOffsetCommitCallback());
                            count = 0;
                        }
                    }
                    int size = bulkRequest.numberOfActions();
                    if (size != 0) {
                        bulkRequest.execute().actionGet();
                    }
                    LOGGER.info("total record: {}, indexed {} records to es", records.count(), size);
                    bulkRequest = transportClient.prepareBulk();
                    kafkaConsumer.commitAsync(currentOffsets, new KafkaOffsetCommitCallback());
                }
            }
        } catch (WakeupException e) {
            // do not process, this is shutdown
            LOGGER.error("wakeup, start to shutdown, {}", e);
        } catch (Exception e) {
            LOGGER.error("process records error, {}", e);
        } finally {
            kafkaConsumer.commitSync(currentOffsets);
            LOGGER.info("finally commit the offset");
            // 不需要主动调kafkaConsumer.close(), spring bean容器会调用
        }
    }

    @Override
    public Thread executeThread() {
        return this.thread;
    }

    /**
     * 根据log字符串构造XContentBuilder
     * @param line
     * @return
     */
    private XContentBuilder buildXContentBuilder(String line) {
        try {
            LogDto logDto = new LogDto(line);
            return jsonBuilder()
                    .startObject()
                    .field(Constants.DAY, logDto.getDay())
                    .field(Constants.TIME, logDto.getTime())
                    .field(Constants.NANOTIME, logDto.getNanoTime())
                    .field(Constants.CREATED, logDto.getCreated())
                    .field(Constants.APP, logDto.getApp())
                    .field(Constants.HOST, logDto.getHost())
                    .field(Constants.THREAD, logDto.getThread())
                    .field(Constants.LEVEL, logDto.getLevel())
                    .field(Constants.EVENT_TYPE, logDto.getEventType())
                    .field(Constants.PACK, logDto.getPack())
                    .field(Constants.CLAZZ, logDto.getClazz())
                    .field(Constants.LINE, logDto.getLine())
                    .field(Constants.MESSAGE_SMART, logDto.getMessageSmart())
                    .field(Constants.MESSAGE_MAX, logDto.getMessageMax())
                    .endObject();
        } catch (Exception e) {
            return null;
        }
    }
}
