package com.jthink.skyeye.collector.core.test;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc consumer测试
 * @date 2016-08-24 20:23:16
 */
public class KafkaConsumerTest {

    private static Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerTest.class);

    private static KafkaConsumer<byte[], String> kafkaConsumer;

    private static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<TopicPartition, OffsetAndMetadata>();

    private static int count = 0;

    private static Thread thread;

    public static void main(String[] args) {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "arreat00:9092,arreat05:9092,arreat08:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "es-indexer-consume-group");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        kafkaConsumer = new KafkaConsumer<byte[], String>(config);
        kafkaConsumer.subscribe(Arrays.asList("app-log-test-2"), new HandleRebalance());

        thread = Thread.currentThread();


        Settings settings = Settings.settingsBuilder().put("cluster.name", "taurus")
                .put("client.transport.sniff", true).build();
        TransportClient client = TransportClient.builder().settings(settings).build();
        try {
            String[] ips = "192.168.88.143,192.168.88.145,192.168.88.148".split(",");
            for (String ip : ips) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), 9300));
            }
        } catch (UnknownHostException e) {
            LOGGER.error("es集群主机名错误");
        }


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                LOGGER.info("Starting exit...");
                kafkaConsumer.wakeup();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            while (true) {
                BulkRequestBuilder bulkRequest = client.prepareBulk();
                ConsumerRecords<byte[], String> records = kafkaConsumer.poll(100);
//                LOGGER.info(records.count() + "");
//                LOGGER.info("count" + count);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<byte[], String> record : records) {
                        String value = record.value();
//                    LOGGER.info("value: {}", value);
                        bulkRequest.add(client.prepareIndex("log-test0919", "log").setSource(buildXContentBuilder(value)));
//                    LOGGER.info("topic = {}, partition = {}, offset = {}, customer = {}, country = {}",
//                            record.topic(), record.partition(), record.offset(), record.key(), record.value());
                        LOGGER.info(record.offset() + "");
                        currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));

                        if (count % 1000 == 0) {
                            // 当达到了1000触发向kafka提交offset
                            kafkaConsumer.commitAsync(currentOffsets, new OffsetCommitCallback() {
                                @Override
                                public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                                    if (null != exception) {
                                        // 如果异步提交发生了异常
                                        LOGGER.error("commit failed for offsets {}, and exception is {}", offsets, exception);
                                    }
                                }
                            });
                        }
                        count++;
                    }
                    if (count >= 1000000) {
                        count = 0;
                    }
                    LOGGER.info("indexed {} records to es", records.count());
                    bulkRequest.execute().actionGet();
                }
            }
        } catch (WakeupException e) {
            // do not process, this is shutdown
            LOGGER.error("wakeup, start to shutdown, ", e);
        } catch (Exception e) {
            LOGGER.error("process records error");
        } finally {
            try {
                kafkaConsumer.commitSync(currentOffsets);
                LOGGER.info("finally commit the offset");
            } finally {
                kafkaConsumer.close();
            }
        }
    }

    private static class HandleRebalance implements ConsumerRebalanceListener {

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            kafkaConsumer.commitSync(currentOffsets);
            LOGGER.info("before rebalance, commit offset once");
        }

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

        }

    }

    private static XContentBuilder buildXContentBuilder(String line) throws IOException {

        LogDto logDto = new LogDto(line);
        return jsonBuilder()
                .startObject()
                .field("day", logDto.getDay())
                .field("time", logDto.getTime())
                .field("app", logDto.getApp())
                .field("host", logDto.getHost())
                .field("thread", logDto.getThread())
                .field("level", logDto.getLevel())
                .field("pack", logDto.getPack())
                .field("clazz", logDto.getClazz())
                .field("line", logDto.getLine())
                .field("message_smart", logDto.getMessage())
                .field("message_max", logDto.getMessage())
                .endObject();
    }

    static class LogDto {
        private String day;
        private String time;
        private String app;
        private String host;
        private String thread;
        private String level;
        private String pack;
        private String clazz;
        private String line;
        private String message;

        public LogDto(String log) {
            String[] detail = log.split(";", 8);
            String date = detail[0];
            this.day = date.substring(0, 10).trim();
            this.time = date.substring(11, date.indexOf(".")).trim();
            this.app = detail[1].trim();
            this.host = detail[2].trim();
            this.thread = detail[3].trim();
            this.level = detail[4].trim();
            String packClazz = detail[5];
            this.pack = packClazz.substring(0, packClazz.lastIndexOf("."));
            this.clazz = packClazz.substring(packClazz.lastIndexOf(".") + 1).trim();
            this.line = detail[6].trim();
            this.message = detail[7].trim();
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getThread() {
            return thread;
        }

        public void setThread(String thread) {
            this.thread = thread;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getPack() {
            return pack;
        }

        public void setPack(String pack) {
            this.pack = pack;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
