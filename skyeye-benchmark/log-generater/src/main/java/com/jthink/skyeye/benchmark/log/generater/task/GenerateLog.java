package com.jthink.skyeye.benchmark.log.generater.task;

import com.jthink.skyeye.benchmark.log.generater.service.GenerateLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-11-28 15:28:52
 */
@Configuration
@EnableScheduling
public class GenerateLog {

    private static Logger LOGGER = LoggerFactory.getLogger(GenerateLog.class);

    @Autowired
    private GenerateLogService generateLogService;

    @Scheduled(cron = "* * * * * *")
    public void refreshCache() {
//        LOGGER.info("reload cache start");
//        this.cacheService.load();
//        LOGGER.info("reload cache end");

//        for (int i = 0; i < 10; i ++) {
            this.generateLogService.generateCoverLog();
//        }
    }
}
