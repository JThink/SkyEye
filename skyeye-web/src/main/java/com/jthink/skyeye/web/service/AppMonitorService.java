package com.jthink.skyeye.web.service;

import com.jthink.skyeye.data.jpa.dto.AppStatusDto;
import com.jthink.skyeye.data.jpa.repository.AppInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc app监控的service
 * @date 2016-10-09 09:15:34
 */
@Service
public class AppMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppMonitorService.class);

    @Autowired
    private AppInfoRepository appInfoRepository;

    /**
     * 根据host和app type查询状态信息
     * @param host
     * @param app
     * @param type
     * @return
     */
    public List<AppStatusDto> getHostAppInfo(String host, String app, int type) {
        if (null == host) {
            return this.appInfoRepository.findBySql(type);
        } else {
            if (null == app) {
                return this.appInfoRepository.findBySql(host, type);
            } else {
                return this.appInfoRepository.findBySql(host, app, type);
            }
        }
    }

    /**
     * 根据host和app type查询状态信息
     * @param host
     * @param app
     * @param type
     * @return
     */
    public List<AppStatusDto> getAppHostInfo(String host, String app, int type) {
        if (null == app) {
            return this.appInfoRepository.findBySql(type);
        } else {
            if (null == host) {
                return this.appInfoRepository.findBySqlApp(app, type);
            } else {
                return this.appInfoRepository.findBySql(host, app, type);
            }
        }
    }

    /**
     * 获得所有的host和app
     * @param type
     * @return
     */
    public Map<String, List<String>> getHostApp(int type) {
        Map<String, List<String>> hostApps = new HashMap<String, List<String>>();
        List<AppStatusDto> appStatusDtos = this.appInfoRepository.findBySql(type);
        List<String> apps = null;
        for (AppStatusDto dto : appStatusDtos) {
            String host = dto.getHost();
            if (hostApps.containsKey(host)) {
                // 如果已经存在该host
                List<String> existApps = hostApps.get(host);
                existApps.add(dto.getApp());
            } else {
                // 未存在host
                apps = new ArrayList<String>();
                apps.add(dto.getApp());
                hostApps.put(host, apps);
            }
        }
        return hostApps;
    }

    /**
     * 获得所有的host和app
     * @param type
     * @return
     */
    public Map<String, List<String>> getAppHost(int type) {
        Map<String, List<String>> appHosts = new HashMap<String, List<String>>();
        List<AppStatusDto> appStatusDtos = this.appInfoRepository.findBySql(type);
        List<String> hosts = null;
        for (AppStatusDto dto : appStatusDtos) {
            String app = dto.getApp();
            if (appHosts.containsKey(app)) {
                // 如果已经存在该app
                List<String> existHosts = appHosts.get(app);
                existHosts.add(dto.getHost());
            } else {
                hosts = new ArrayList<String>();
                hosts.add(dto.getHost());
                appHosts.put(app, hosts);
            }
        }
        return appHosts;
    }
}
