package com.jthink.skyeye.base.util;

import com.jthink.skyeye.base.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc docker部署app情况下获取host的工具包
 * @date 2017-12-07 19:38:39
 */
public class DockerHostUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerHostUtil.class);

    /**
     * 从运行机器获取host
     * @return
     */
    public static String getHostFromLocal() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage();
            if (host != null) {
                int colon = host.indexOf(Constants.COLON);
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }

        }
        return Constants.UNKNOWN_HOST;
    }

    /**
     * 将host写入到
     * @param host
     * @return
     */
    public static void writeToFile(String host) {
        BufferedWriter bw = null;
        try {
            File file = new File(Constants.SKYEYE_HOST_FILE);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    LOGGER.info("创建父文件夹失败");
                }
            }
            bw = new BufferedWriter(new FileWriter(file, false));
            bw.write(host);
        } catch (IOException e) {
            LOGGER.info("写文件报错, ", e);
        } finally {
            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    LOGGER.info("写文件报错, ", e);
                }
            }
        }
    }

    /**
     * 从文件中读取host
     * @return
     */
    public static String readFromFile() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(Constants.SKYEYE_HOST_FILE));
            return br.readLine();
        } catch (IOException e) {
            LOGGER.error("读文件报错, ", e);
        } catch (Exception e) {
            LOGGER.error("解密出错, ", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOGGER.info("读文件报错, ", e);
                }
            }
        }
        return null;
    }
}
