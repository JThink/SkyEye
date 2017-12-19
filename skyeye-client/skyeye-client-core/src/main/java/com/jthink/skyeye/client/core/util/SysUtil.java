package com.jthink.skyeye.client.core.util;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.util.DockerHostUtil;
import com.jthink.skyeye.base.util.StringUtil;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 系统相关的util
 * @date 2016-09-27 19:38:39
 */
public class SysUtil {

    public static String host = Constants.EMPTY_STR;
    public static String userDir = Constants.EMPTY_STR;

    static {
        // host的读取，为了配合容器部署和rancher部署，首先是需要从环境变量里面取，如果取不到再从.skyeye/host这个文件取，最后再取不到从运行机器中取
        host = System.getenv(Constants.COMPUTERNAME);
        if (StringUtil.isBlank(host)) {
            // 未获取到, 从docker设置的环境变量取
            host = System.getenv(Constants.SKYEYE_HOST_TO_REGISTRY);
            if (StringUtil.isBlank(host)) {
                // 未获取到，从.skyeye/host获取
                host = DockerHostUtil.readFromFile();
                if (StringUtil.isBlank(host)) {
                    // 未获取到，从运行机器中取
                    host = DockerHostUtil.getHostFromLocal();
                }
            }
        }

        userDir = System.getProperty("user.dir", "<NA>");
    }

}
