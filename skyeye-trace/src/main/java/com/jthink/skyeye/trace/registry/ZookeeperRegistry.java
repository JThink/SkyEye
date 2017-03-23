package com.jthink.skyeye.trace.registry;

import com.jthink.skyeye.trace.dto.RegisterDto;
import org.I0Itec.zkclient.ZkClient;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 利用zookeeper实现注册中心，单例确保只进行一次注册
 * @date 2017-03-23 10:14:22
 */
public class ZookeeperRegistry implements Registry {

    @Override
    public String register(RegisterDto registerDto) {
        // TODO: 具体的注册方法
        // 1. 读取部署的机器host、app name
        String host = "host";
        String name = "skyeye-trace-test";

        // 向注册中心注册
        ZkClient zkClient = new ZkClient(registerDto.getZkServers(), 60000, 5000);
//        zkClient.writeData();

        return null;
    }
}
