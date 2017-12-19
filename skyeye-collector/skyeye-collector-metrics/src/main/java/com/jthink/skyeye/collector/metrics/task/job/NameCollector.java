package com.jthink.skyeye.collector.metrics.task.job;

import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.constant.MiddleWare;
import com.jthink.skyeye.base.constant.NameInfoType;
import com.jthink.skyeye.base.dto.ApiLog;
import com.jthink.skyeye.base.dto.EventLog;
import com.jthink.skyeye.base.dto.LogDto;
import com.jthink.skyeye.collector.metrics.cache.CacheService;
import com.jthink.skyeye.data.jpa.domain.NameInfo;
import com.jthink.skyeye.data.jpa.pk.NameInfoPK;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 名称采集job，包含api name、account name、third name
 * @date 2016-11-21 17:23:30
 */
public class NameCollector extends Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(NameCollector.class);

    private CacheService cacheService;

    public NameCollector(List<EventType> types) {
        super(types);
    }

    @Override
    public void doJob(EventLog log, LogDto logDto, BulkRequestBuilder bulkRequest) {
        // 进行api name、account以及third name的收集，由于大量数据中仅仅存在少量需要采集的数据，无需每次采集都入库，可以做内存缓存和系统缓存进行是否已经入库判定
        EventType type = log.getEventType();
        if (this.getTypes().indexOf(type) != -1) {
            // 符合该job的消息类型
            String app = logDto.getApp();
            if (log instanceof ApiLog) {
                // api的采集
                ApiLog apiLog = (ApiLog) log;
                String apiSymbol = NameInfoType.API.symbol();
                String api = apiLog.getUniqueName();
                String accountSymbol = NameInfoType.ACCOUNT.symbol();
                String account = apiLog.getAccount();
                if (!this.cacheService.isExists(apiSymbol, api)) {
                    // 如果api不存在
                    LOGGER.info("从app {}中采集到api, 为: {}", app, api);
                    this.cacheService.save(this.buildNameInfo(api, apiSymbol, app, 1));
                    this.cacheService.add(apiSymbol, api);
                }
                if (!this.cacheService.isExists(accountSymbol, account)) {
                    // 如果account不存在
                    LOGGER.info("从app {}中采集到account, 为: {}", app, account);
                    this.cacheService.save(this.buildNameInfo(account, accountSymbol, app));
                    this.cacheService.add(accountSymbol, account);
                }
            } else {
                String uniqueName = log.getUniqueName();
                if (type == EventType.thirdparty_call) {
                    // 第三方
                    String thirdSymbol = NameInfoType.THIRD.symbol();
                    if (!this.cacheService.isExists(thirdSymbol, uniqueName)) {
                        // 如果third不存在
                        LOGGER.info("从app {}中采集到third party, 为: {}", app, uniqueName);
                        this.cacheService.save(this.buildNameInfo(uniqueName, thirdSymbol, app, 2));
                        this.cacheService.add(thirdSymbol, uniqueName);
                    }
                } else if (type == EventType.middleware_opt) {
                    // 中间件
                    String middlewareSymbol = NameInfoType.MIDDLEWARE.symbol();
                    if (!this.cacheService.isExists(middlewareSymbol, uniqueName) &&
                            (uniqueName.equals(MiddleWare.HBASE.symbol()) || uniqueName.equals(MiddleWare.MONGO.symbol()))) {
                        // TODO: 当前只支持hbase和mongo，以后支持多了该判断方式需要修改
                        // 如果middleware不存在
                        LOGGER.info("从app {}中采集到middleware, 为: {}", app, uniqueName);
                        this.cacheService.save(this.buildNameInfo(uniqueName, middlewareSymbol, app, 3));
                        this.cacheService.add(middlewareSymbol, uniqueName);
                    }
                }
            }
        }

        // 进行后续的处理
        if (null != this.getNextJob()) {
            this.getNextJob().doJob(log, logDto, bulkRequest);
        }
    }

    /**
     * 构造NameInfo
     * @param name
     * @param type
     * @param app
     * @return
     */
    private NameInfo buildNameInfo(String name, String type, String app) {
        NameInfoPK nameInfoPK = new NameInfoPK();
        nameInfoPK.setName(name);
        nameInfoPK.setType(type);
        NameInfo nameInfo = new NameInfo();
        nameInfo.setApp(app);
        nameInfo.setNameInfoPK(nameInfoPK);
        return nameInfo;
    }

    /**
     * 构造NameInfo
     * @param name
     * @param type
     * @param app
     * @param tid
     * @return
     */
    private NameInfo buildNameInfo(String name, String type, String app, Integer tid) {
        NameInfoPK nameInfoPK = new NameInfoPK();
        nameInfoPK.setName(name);
        nameInfoPK.setType(type);
        NameInfo nameInfo = new NameInfo();
        nameInfo.setApp(app);
        nameInfo.setNameInfoPK(nameInfoPK);
        nameInfo.setTid(tid);
        return nameInfo;
    }

    public CacheService getCacheService() {
        return cacheService;
    }

    public void setCacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

}
