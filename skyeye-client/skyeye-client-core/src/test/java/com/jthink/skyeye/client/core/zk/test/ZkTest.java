package com.jthink.skyeye.client.core.zk.test;

import com.jthink.skyeye.base.constant.Constants;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-09-21 15:44:21
 */
public class ZkTest {

    public static void main(String[] args) {

        String zkHost = "192.168.88.196:2182,192.168.88.197:2182,192.168.88.198:2182";

        // 进行监控
        ZkClient zkClient = new ZkClient(zkHost, 60000, 5000);

        System.out.println("你妹的: " + zkClient.readData(Constants.ROOT_PATH_PERSISTENT + Constants.SLASH + "app-test"));



        List<String> apps = zkClient.getChildren(Constants.ROOT_PATH_EPHEMERAL);
        for (String app : apps) {
            zkClient.subscribeChildChanges(Constants.ROOT_PATH_EPHEMERAL + Constants.SLASH + app, new AppChildChangeListener());
            List<String> hosts = zkClient.getChildren(Constants.ROOT_PATH_EPHEMERAL + Constants.SLASH + app);
            for (String host : hosts) {
                zkClient.subscribeDataChanges(Constants.ROOT_PATH_EPHEMERAL + Constants.SLASH + app + Constants.SLASH + host, new HostDataChangeListener());
                System.out.println();
            }
        }

        synchronized (ZkTest.class) {
            while (true) {
                try {
                    ZkTest.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class HostDataChangeListener implements IZkDataListener {

        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
            System.out.println("path: " + dataPath + " data: " + data);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {

        }
    }

    private static class AppChildChangeListener implements IZkChildListener {

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            System.out.println("parentPath: " + parentPath);
            for (String c : currentChilds) {
                System.out.println("child: " + c);
            }
        }
    }

}
