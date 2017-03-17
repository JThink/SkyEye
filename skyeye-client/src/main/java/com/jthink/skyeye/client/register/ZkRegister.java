package com.jthink.skyeye.client.register;

import com.jthink.skyeye.client.constant.NodeMode;
import org.I0Itec.zkclient.ZkClient;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc zookeeper注册中心
 * @date 2016-09-21 14:03:46
 */
public class ZkRegister {

    // zkClient
    private ZkClient client;

    public ZkRegister(ZkClient client) {
        this.client = client;
    }

    /**
     * 创建节点
     * @param path
     * @param nodeMode
     */
    public void create(String path, NodeMode nodeMode) {
        if (nodeMode.symbol().equals(NodeMode.PERSISTENT.symbol())) {
            // 创建永久节点
            this.client.createPersistent(nodeMode.label() + path, true);
        } else if (nodeMode.symbol().equals(NodeMode.EPHEMERAL.symbol())) {
            // 创建临时节点
            this.client.createEphemeral(nodeMode.label() + path);
        }
    }

    /**
     * 创建带data的节点
     * @param path
     * @param nodeMode
     * @param data
     */
    public void create(String path, NodeMode nodeMode, String data) {
        if (nodeMode.symbol().equals(NodeMode.PERSISTENT.symbol())) {
            // 创建永久节点，加入数据
            this.client.createPersistent(nodeMode.label() + path, true);
        } else if (nodeMode.symbol().equals(NodeMode.EPHEMERAL.symbol())) {
            // 创建临时节点，加入数据
            this.client.createEphemeral(nodeMode.label() + path, data);
        }
    }

    /**
     * 写节点数据
     * @param path
     * @param nodeMode
     * @param data
     */
    public void write(String path, NodeMode nodeMode, String data) {
        if (nodeMode.symbol().equals(NodeMode.PERSISTENT.symbol())) {
            // 创建永久节点，加入数据
            this.client.writeData(nodeMode.label() + path, true);
        } else if (nodeMode.symbol().equals(NodeMode.EPHEMERAL.symbol())) {
            // 创建临时节点，加入数据
            this.client.writeData(nodeMode.label() + path, data);
        }
    }

    public ZkClient getClient() {
        return client;
    }

    public void setClient(ZkClient client) {
        this.client = client;
    }
}
