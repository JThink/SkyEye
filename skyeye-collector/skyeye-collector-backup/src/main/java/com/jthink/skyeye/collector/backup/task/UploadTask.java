package com.jthink.skyeye.collector.backup.task;

import com.jthink.skyeye.base.util.DateUtil;
import com.jthink.skyeye.collector.backup.util.FileUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 数据上传hdfs任务
 * @date 2016-12-06 17:51:33
 */
@Component
@EnableScheduling
public class UploadTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadTask.class);

    @Autowired
    private FileUtil fileUtil;

    /**
     * 上传到hdfs并删除相应的文件
     */
    @Scheduled(cron = "${spring.upload.log.cron}")
    private void upload() {
        String yesterday = this.getYesterday();
        LOGGER.info("开始上传到hdfs, 时间: {}", yesterday);
        StopWatch sw = new StopWatch();
        sw.start();
        this.fileUtil.uploadToHDFS(yesterday);
        sw.stop();
        LOGGER.info("上传到hdfs结束, 耗时: {} ms", sw.getTotalTimeMillis());
    }

    /**
     * 返回昨天的字符串
     * @return
     */
    private String getYesterday() {
        DateTime yesterday = new DateTime(System.currentTimeMillis()).minusDays(1);
        return yesterday.toString(DateUtil.YYYYMMDD);
    }

}
