package com.jthink.skyeye.collector.core.task;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka消费task
 * @date 2016-09-20 10:24:24
 */
public interface Task extends Runnable {

    /**
     * 执行task
     */
    void doTask();

    Thread executeThread();
}
