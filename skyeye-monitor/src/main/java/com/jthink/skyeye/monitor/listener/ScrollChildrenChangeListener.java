package com.jthink.skyeye.monitor.listener;

import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import com.jthink.skyeye.monitor.service.AppInfoService;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc app的root节点变化监听
 * @date 2016-09-23 14:49:36
 */
public class ScrollChildrenChangeListener implements PathChildrenCacheListener  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScrollChildrenChangeListener.class);

    private RabbitmqService rabbitmqService;

    private ZkClient zkClient;

    private AppInfoService appInfoService;

    public ScrollChildrenChangeListener(RabbitmqService rabbitmqService, ZkClient zkClient, AppInfoService appInfoService) {
        this.rabbitmqService = rabbitmqService;
        this.zkClient = zkClient;
        this.appInfoService = appInfoService;
    }

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        switch (event.getType()) {
            case CHILD_ADDED:
                PathChildrenCache pathChildrenCache = new PathChildrenCache(client, event.getData().getPath(), true);
                pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                pathChildrenCache.getListenable().addListener(new AppChildrenChangeListener(this.rabbitmqService, this.zkClient, this.appInfoService));
                LOGGER.info("app added: " + event.getData().getPath());
                break;
            case CHILD_REMOVED:
                LOGGER.info("app removed: " + event.getData().getPath());
                break;
        }
    }
}
