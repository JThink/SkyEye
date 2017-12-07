package com.jthink.skyeye.agent.launcher;

import com.jthink.skyeye.base.util.DockerHostUtil;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 项目启动器
 * @date 2016-08-24 18:31:48
 */
public class Launcher {

    public static void main(String[] args) {
        // 进行宿主机host获取, 并写入
        String host = DockerHostUtil.getHostFromLocal();
        DockerHostUtil.writeToFile(host);
    }
}
