package com.jthink.skyeye.collector.metrics.task.job;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.dto.ApiLog;
import com.jthink.skyeye.base.dto.EventLog;
import com.jthink.skyeye.base.dto.LogDto;
import com.jthink.skyeye.base.util.DateUtil;
import com.jthink.skyeye.collector.core.configuration.es.EsProperties;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 监控相关信息入es的job
 * @date 2016-11-21 17:21:17
 */
public class Indexer extends Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(Indexer.class);

    private TransportClient transportClient;
    private EsProperties esProperties;

    public Indexer(List<EventType> types) {
        super(types);
    }

    @Override
    public void doJob(EventLog log, LogDto logDto, BulkRequestBuilder bulkRequest) {
        // 进行索引（for kibana），包含api调用、第三方调用、中间件调用
        if (this.getTypes().indexOf(log.getEventType()) != -1 && (log.getStatus().equals(EventLog.MONITOR_STATUS_FAILED) || log.getStatus().equals(EventLog.MONITOR_STATUS_SUCCESS))) {
            try {
                bulkRequest.add(transportClient.prepareIndex(this.esProperties.getIndex(), this.esProperties.getDoc())
                        .setSource(this.buildXContentBuilder(log, logDto)));
            } catch (IOException e) {
                LOGGER.error("构造一条es入库数据失败, {]", logDto);
            }
        }

        // 进行后续的处理
        if (null != this.getNextJob()) {
            this.getNextJob().doJob(log, logDto, bulkRequest);
        }
    }

    /**
     * 根据log相关信息构造XContentBuilder
     * @param log
     * @param logDto
     * @return
     * @throws IOException
     */
    private XContentBuilder buildXContentBuilder(EventLog log, LogDto logDto) throws IOException {
        String day = logDto.getDay();
        String[] ymd = day.split(Constants.MIDDLE_LINE);
        String account = Constants.EMPTY_STR;
        if (log instanceof ApiLog) {
            account = ((ApiLog) log).getAccount();
        }
        return jsonBuilder()
                .startObject()
                .field(Constants.CREATED, logDto.getCreated())
                .field(Constants.TIME, this.getTime(day, logDto.getTime()))
                .field(Constants.DAY, day)
                .field(Constants.WEEK, DateUtil.getWeek(day))
                .field(Constants.MONTH, ymd[0] + Constants.MIDDLE_LINE + ymd[1])
                .field(Constants.YEAR, ymd[0])
                .field(Constants.APP, logDto.getApp())
                .field(Constants.HOST, logDto.getHost())
                .field(Constants.EVENT_TYPE, log.getEventType().symbol())
                .field(Constants.ACCOUNT, account)
                .field(Constants.UNIQUE_NAME, log.getUniqueName())
                .field(Constants.COST, log.getCost())
                .field(Constants.STATUS, log.getStatus())
                .field(Constants.MESSAGE_SMART, log.getLog())
                .field(Constants.MESSAGE_MAX, log.getLog())
                .endObject();
    }

    /**
     * 根据给定的day和time返回时间（2016-11-23 16:42:40）
     * @param day
     * @param time
     * @return
     */
    private String getTime(String day, String time) {
        return day + Constants.SPACE + time.substring(0, time.lastIndexOf("."));
    }

    public TransportClient getTransportClient() {
        return transportClient;
    }

    public void setTransportClient(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    public EsProperties getEsProperties() {
        return esProperties;
    }

    public void setEsProperties(EsProperties esProperties) {
        this.esProperties = esProperties;
    }
}
