package com.jthink.skyeye.monitor.service;

import com.jthink.skyeye.data.jpa.domain.AppInfo;
import com.jthink.skyeye.data.jpa.pk.AppInfoPK;
import com.jthink.skyeye.data.jpa.repository.AppInfoRepository;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.LogCollectionStatus;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-10-09 11:09:01
 */
@Service
public class AppInfoService {

    @Autowired
    private ZkClient zkClient;
    @Autowired
    private AppInfoRepository appInfoRepository;

    /**
     * 保存appInfo
     * @param host
     * @param app
     * @param type
     * @param logCollectionStatus
     */
    public void add(String host, String app, int type, LogCollectionStatus logCollectionStatus) {
        AppInfo appInfo = new AppInfo();
        AppInfoPK appInfoPK = new AppInfoPK(host, app, type);
        appInfo.setAppInfoPK(appInfoPK);
        appInfo.setStatus(logCollectionStatus.symbol());
        if (logCollectionStatus.symbol().equals(LogCollectionStatus.HISTORY.symbol())) {
            appInfo.setDeploy(this.getDeploy(Constants.ROOT_PATH_PERSISTENT + Constants.SLASH + app + Constants.SLASH + host));
        } else {
            appInfo.setDeploy(this.getDeploy(Constants.ROOT_PATH_EPHEMERAL + Constants.SLASH + app + Constants.SLASH + host));
        }
        this.appInfoRepository.save(appInfo);
    }

    /**
     * 修改记录的收集日志状态
     * @param host
     * @param app
     * @param type
     * @param logCollectionStatus
     */
    public void update(String host, String app, int type, LogCollectionStatus logCollectionStatus) {
        AppInfo appInfo = this.appInfoRepository.findOne(new AppInfoPK(host, app, type));
        appInfo.setStatus(logCollectionStatus.symbol());
        this.appInfoRepository.save(appInfo);
    }

    /**
     * 根据host和app进行删除
     * @param host
     * @param app
     * @param type
     */
    public void delete(String host, String app, int type) {
        AppInfo appInfo = this.appInfoRepository.findOne(new AppInfoPK(host, app, type));
        if (null != appInfo) {
            this.appInfoRepository.delete(appInfo);
        }
    }

    /**
     * 删除所有的数据
     */
    public void deleteAll() {
        this.appInfoRepository.deleteAll();
    }

    /**
     * 获取app的部署位置
     * @param path
     * @return
     */
    private String getDeploy(String path) {
        String[] datas = this.zkClient.readData(path).toString().split(Constants.SEMICOLON);
        return  datas[datas.length - 1];
    }
}
