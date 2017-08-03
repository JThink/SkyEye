package com.jthink.skyeye.collector.core.hook;

import com.jthink.skyeye.collector.core.task.Task;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 项目启动器
 * @date 2017-08-03 18:31:48
 * @since 1.0.0
 */
public class ShutdownHookRunner extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHookRunner.class);

    private KafkaConsumer kafkaConsumer;

    private Task task;

    public ShutdownHookRunner(KafkaConsumer kafkaConsumer, Task task) {
        this.kafkaConsumer = kafkaConsumer;
        this.task = task;
    }

    @Override
    public void run() {
        LOGGER.info("starting to exit");

        this.kafkaConsumer.wakeup();

        try {
            this.task.executeThread().join();
        } catch (InterruptedException e) {
            LOGGER.error("interrupted, ", e);
        }
    }
}
