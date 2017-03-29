package com.jthink.skyeye.monitor.test;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc zk框架curator测试代码
 * @date 2016-09-22 14:38:59
 */
public class CuratorTest {


    public static void main(String[] args) throws Exception {

        String zkServers = "192.168.88.196:2182,192.168.88.197:2182,192.168.88.198:2182";

        final String rootPath = "/up_monitor/scroll";

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework curatorFramework = CuratorFrameworkFactory.builder().
                connectString(zkServers).
                sessionTimeoutMs(60000).
                connectionTimeoutMs(5000).
                retryPolicy(retryPolicy).
                build();

        curatorFramework.start();

        List<String> apps = curatorFramework.getChildren().forPath(rootPath);

        // 启动时获取所有的节点数据
        final List<String> appHosts = new ArrayList<String>();
        for (String app : apps) {
            List<String> hosts = curatorFramework.getChildren().forPath(rootPath + "/" + app);
            for (String host : hosts) {
                appHosts.add(rootPath + "/" + app + "/" + host);
            }
        }

        PathChildrenCache rootCache = new PathChildrenCache(curatorFramework, rootPath, true);
        rootCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        rootCache.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                switch (event.getType()) {
                    case CHILD_ADDED:
                        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, event.getData().getPath(), true);
                        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {

                            @Override
                            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                                String node = null;
                                switch (event.getType()) {
                                    case CHILD_ADDED:
                                        node = event.getData().getPath();
                                        if (!appHosts.contains(node)) {
                                            System.out.println("CHILD_ADDED: " + event.getData().getPath());
                                            appHosts.add(node);
                                        }
                                        break;
                                    case CHILD_REMOVED:
                                        node = event.getData().getPath();
                                        System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                                        if (appHosts.contains(node)) {
                                            appHosts.remove(node);
                                        }
                                        break;
                                    case CHILD_UPDATED:
                                        System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                                        System.out.println("CHILD_UPDATED: " + event.getData().getData());
                                        break;
                                }
                            }
                        });
                        System.out.println("ROOT CHILD_ADDED: " + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("ROOT CHILD_REMOVED: " + event.getData().getPath());
                        break;
                }
            }
        });

        synchronized (CuratorTest.class) {
            while (true) {
                try {
                    CuratorTest.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
