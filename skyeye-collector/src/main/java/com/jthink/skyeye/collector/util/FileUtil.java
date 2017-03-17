package com.jthink.skyeye.collector.util;

import com.jthink.skyeye.collector.configuration.hadoop.HadoopProperties;
import com.jthink.skyeye.collector.configuration.kafka.KafkaProperties;
import com.jthink.skyeye.base.constant.Constants;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc file 相关的util
 * @date 2016-12-06 11:26:33
 */
@Component
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    @Autowired
    private KafkaProperties kafkaProperties;
    @Autowired
    private HadoopProperties hadoopProperties;
    @Autowired
    private FileSystem fileSystem;

    /**
     * 将数据写入文件
     * @param lines
     */
    public int save(Map<String, List<String>> lines) {
        int sum = 0;
        for (Map.Entry<String, List<String>> entry : lines.entrySet()) {
            this.writeTofile(entry.getKey(), entry.getValue());
            sum += entry.getValue().size();
        }
        return sum;
    }

    /**
     * 写入文件
     * @param fileName
     * @param lines
     */
    private void writeTofile(String fileName, List<String> lines) {
        BufferedWriter bw = null;
        try {
            File file = new File(this.kafkaProperties.getFileRoot() + this.getFileName(fileName));
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    LOGGER.info("创建父文件夹失败");
                }
            }
            bw = new BufferedWriter(new FileWriter(file, true));
            for (String line : lines) {
                bw.write(line);
            }
        } catch (IOException e) {
            LOGGER.error("写文件报错, ", e);
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    LOGGER.error("写文件报错, ", e);
                }
            }
        }
    }

    /**
     * 上传至hdfs
     */
    public void uploadToHDFS(String yesterday) {
        try {
            String fileName = this.getFileName(yesterday);
            File file = new File(this.kafkaProperties.getFileRoot() + fileName);
            if (!file.exists()) {
                LOGGER.info("当天没有可上传的文件");
                return;
            }
            this.fileSystem.copyFromLocalFile(true, false, new Path(this.kafkaProperties.getFileRoot() + fileName),
                    new Path(this.hadoopProperties.getBaseDir() + yesterday + Constants.SLASH + fileName));
        } catch (IOException e) {
            LOGGER.error("上传至hdfs失败, ", e);
        }
    }

    /**
     * 返回具体的名字
     * @param fileName
     * @return
     */
    private String getFileName(String fileName) {
        return fileName + Constants.POINT + this.kafkaProperties.getServerId();
    }


}
