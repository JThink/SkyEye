package com.jthink.skyeye.collector.backup.configuration.hadoop;

import com.jthink.skyeye.base.constant.Constants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-06 18:12:48
 */
@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(HadoopProperties.class)
public class HadoopConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopConfiguration.class);

    @Autowired
    private HadoopProperties hadoopProperties;

    @Bean
    public FileSystem fileSystem() {
        String hdfsAdd = Constants.PROTOCOL + Constants.TERMINAL + Constants.FLAG + this.hadoopProperties.getHost() + Constants.TERMINAL + this.hadoopProperties.getPort();
        // 加载默认配置文件
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        try {
            // 获得hdfs文件系统
            return FileSystem.newInstance(URI.create(hdfsAdd), conf, this.hadoopProperties.getUser());
        } catch (IOException e) {
            LOGGER.error("创建FileSystem失败");
            return null;
        } catch (InterruptedException e) {
            LOGGER.error("创建FileSystem失败");
            return null;
        }
    }
}
