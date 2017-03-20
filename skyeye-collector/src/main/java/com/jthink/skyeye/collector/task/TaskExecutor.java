package com.jthink.skyeye.collector.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 所有的task执行器
 * @date 2016-09-20 10:30:11
 */
@Component
public class TaskExecutor {

    @Autowired
    private Task indexerTask;
    @Autowired
    private Task collectTask;
    @Autowired
    private Task backupTask;
    @Autowired
    private Task rpcTraceTask;

    private List<Task> tasks = null;

    public void addTask() {
        if (null == tasks) {
            tasks = new ArrayList<Task>();
        }
        this.tasks.add(this.indexerTask);
        this.tasks.add(this.collectTask);
        this.tasks.add(this.backupTask);
        this.tasks.add(this.rpcTraceTask);
    }

    /**
     * 执行
     */
    public void execute() {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        for (Task task : tasks) {
            pool.execute(task);
        }
    }

    /**
     * 等待执行完成
     */
    public void join() throws InterruptedException {
        for (Task task : tasks) {
            task.executeThread().join();
        }
    }
}
