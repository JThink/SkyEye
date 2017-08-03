package com.jthink.skyeye.collector.backup.task;

import com.jthink.skyeye.base.constant.LogLevel;
import com.jthink.skyeye.base.dto.LogDto;
import com.jthink.skyeye.collector.backup.util.FileUtil;
import com.jthink.skyeye.collector.core.callback.KafkaOffsetCommitCallback;
import com.jthink.skyeye.collector.core.configuration.kafka.KafkaProperties;
import com.jthink.skyeye.collector.core.task.Task;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-06 10:39:59
 */
@Component
public class BackupTask implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupTask.class);

    @Autowired
    private KafkaConsumer kafkaConsumer;
    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private FileUtil fileUtil;

    public static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<TopicPartition, OffsetAndMetadata>();
    private Thread thread;

    @Override
    public void doTask() {
        this.thread = Thread.currentThread();
        int count = 0;
        try {
            while (true) {
                ConsumerRecords<byte[], String> records = this.kafkaConsumer.poll(this.kafkaProperties.getPollTimeout());
                if (!records.isEmpty()) {
                    Map<String, List<String>> lines = new HashMap<String, List<String>>();
                    for (ConsumerRecord<byte[], String> record : records) {
                        String value = record.value();
                        LogDto logDto = this.getLogDto(value);
                        if (logDto != null) {
                            if (LogLevel.INFO.isLegal(logDto.getLevel())) {
                                // 是info、error或者warning的日志才进行处理
                                String key = logDto.getDay();
                                if (lines.containsKey(key)) {
                                    // 如果已经存在此天的数据
                                    lines.get(key).add(value);
                                } else {
                                    // 不存在此天的数据
                                    List<String> tmpLines = new ArrayList<String>();
                                    tmpLines.add(value);
                                    lines.put(key, tmpLines);
                                }
                            }
                        } else {
                            LOGGER.info("record transform error, {}", value);
                        }
                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));

                        count++;
                        if (count >= 1000) {
                            // 当达到了1000触发向kafka提交offset
                            this.kafkaConsumer.commitAsync(currentOffsets, new KafkaOffsetCommitCallback());
                            count = 0;
                        }
                    }
                    // save to file
                    int size = this.fileUtil.save(lines);

                    this.kafkaConsumer.commitAsync(currentOffsets, new KafkaOffsetCommitCallback());
                    LOGGER.info("total record: {}, saved {} records to file", records.count(), size);
                }
            }
        } catch (WakeupException e) {
            // do not process, this is shutdown
            LOGGER.error("wakeup, start to shutdown, {}", e);
        } catch (Exception e) {
            LOGGER.error("process records error, {}", e);
        } finally {
            this.kafkaConsumer.commitSync(currentOffsets);
            LOGGER.info("finally commit the offset");
            // 不需要主动调kafkaConsumer.close(), spring bean容器会调用
        }
    }

    @Override
    public Thread executeThread() {
        return this.thread;
    }

    @Override
    public void run() {
        this.doTask();
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
