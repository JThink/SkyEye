package com.jthink.skyeye.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jthink.skyeye.data.http.HttpRequest;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.dto.EventLog;
import com.jthink.skyeye.web.constant.EsSqlTemplate;
import com.jthink.skyeye.web.dto.StatisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-11-29 20:22:54
 */
@Service
@ConfigurationProperties(prefix = "spring.statistic.es")
public class StatisticsService {

    private String url;

    private String realtimeTemplate;

    private String realtimeAllTemplate;

    private String offlineTemplate;

    private String offlineAllTemplate;

    private String searchTemplate;

    private String searchAllTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    /**
     * 根据传递的sql语句查询es
     *
     * @param sql
     * @return
     */
    private String query(String sql) {
        String response = HttpRequest.get(this.url, sql);
        return response;
    }


    /**
     * 根据指定参数返回以天为单位的对应统计信息。
     *
     * @param eventType
     * @param uniqueName
     * @param begin
     * @param end
     * @return
     */
    public List<StatisticsDto> statisticsDay(String eventType, String uniqueName, String begin, String end) {
        return statistics(eventType, uniqueName, begin, end, Constants.DAY, Constants.DAY);
    }

    /**
     * 根据指定参数返回以秒为单位的对应统计信息。
     *
     * @param eventType
     * @param uniqueName
     * @param begin
     * @param end
     * @return
     */
    public List<StatisticsDto> statisticsSec(String eventType, String uniqueName, String begin, String end) {
        return statistics(eventType, uniqueName, begin, end, Constants.TIME, Constants.TIME);
    }

    public List<StatisticsDto> statistics(String eventType, String uniqueName, String begin, String end,
                                          String dateFieldName, String scope) {
        String sql;
        if (uniqueName.equals(Constants.ALL)) {
            sql = this.searchAllTemplate.replace(EsSqlTemplate.EVENTTYPE, eventType)
                    .replace(EsSqlTemplate.BEGIN, begin).replace(EsSqlTemplate.END, end)
                    .replaceAll(EsSqlTemplate.DATEFIELD, dateFieldName)
                    .replace(EsSqlTemplate.SCOPE, scope);
        } else {
            sql = this.searchTemplate.replace(EsSqlTemplate.EVENTTYPE, eventType)
                    .replace(EsSqlTemplate.UNIQUENAME, uniqueName)
                    .replace(EsSqlTemplate.BEGIN, begin).replace(EsSqlTemplate.END, end)
                    .replaceAll(EsSqlTemplate.DATEFIELD, dateFieldName)
                    .replace(EsSqlTemplate.SCOPE, scope);
        }
        return this.parseStatisticsData(this.query(sql), uniqueName, scope);
    }

    /**
     * 解析 es 返回内容
     *
     * @param resp
     * @param groupByFirstField EsSQL 模板中 group by 的第一个字段。(第二个为 status)
     * @return List<StatisticsDto>
     */
    public List<StatisticsDto> parseStatisticsData(String resp, String name, String groupByFirstField) {
        List<StatisticsDto> rtn = new ArrayList<StatisticsDto>();
        if (null != resp) {
            JSONObject json = JSON.parseObject(resp);

            String time;
            int succ, fail;
            Iterator<Object> timeJSONs, statusJSONs;
            JSONObject timeJSON, statusJSON;
            timeJSONs = json.getJSONObject("aggregations").getJSONObject(groupByFirstField)
                    .getJSONArray("buckets").iterator();
            while (timeJSONs.hasNext()) {
                succ = 0;
                fail = 0;

                timeJSON = (JSONObject) timeJSONs.next();
                statusJSONs = timeJSON.getJSONObject("status").getJSONArray("buckets").iterator();
                time = timeJSON.getString("key");
                while (statusJSONs.hasNext()) {
                    statusJSON = (JSONObject) statusJSONs.next();
                    if (statusJSON.getString("key").equals(EventLog.MONITOR_STATUS_SUCCESS)) {
                        succ = statusJSON.getIntValue("doc_count");
                    } else {
                        fail = statusJSON.getIntValue("doc_count");
                    }
                }
                rtn.add(new StatisticsDto(name, time, succ, fail));
            }
        }
        return rtn;
    }


    /**
     * 根据条件构造sql并进行es查询解析返回
     *
     * @param eventType
     * @param uniqueName
     * @param begin
     * @param end
     * @return
     */
    public List<StatisticsDto> offlineStatistics(String eventType, String uniqueName, String begin, String end,
                                                 String scope) {
        String sql = Constants.EMPTY_STR;
        if (uniqueName.equals(Constants.ALL)) {
            sql = this.offlineAllTemplate.replace(EsSqlTemplate.EVENTTYPE, eventType).replace(EsSqlTemplate.BEGIN,
                    begin).replace(EsSqlTemplate.END, end).replace(EsSqlTemplate.SCOPE, scope);
        } else {
            sql = this.offlineTemplate
                    .replace(EsSqlTemplate.EVENTTYPE, eventType)
                    .replace(EsSqlTemplate.UNIQUENAME, uniqueName)
                    .replace(EsSqlTemplate.BEGIN, begin)
                    .replace(EsSqlTemplate.END, end)
                    .replace(EsSqlTemplate.SCOPE, scope);
        }
        return this.parseOfflineData(this.query(sql), scope);
    }

    /**
     * 根据es sql返回的结果进行解析
     *
     * @param response
     * @return
     */
    private List<StatisticsDto> parseOfflineData(String response, String scope) {
        List<StatisticsDto> dtos = new ArrayList<StatisticsDto>();
        if (null == response) {
            // 错误
            return dtos;
        } else {
            JSONObject json = JSON.parseObject(response);
            Iterator<Object> iterator = json.getJSONObject("aggregations").getJSONObject(scope)
                    .getJSONArray("buckets").iterator();

            while (iterator.hasNext()) {
                JSONObject jsonObject = ((JSONObject) iterator.next());
                StatisticsDto dto = new StatisticsDto();
                dto.setTime(jsonObject.getString("key"));

                dtos.add(this.parseStatisticsDto(jsonObject.getJSONObject("status").getJSONArray("buckets").iterator
                        (), dto));
            }
            return dtos;
        }
    }

    /**
     * 根据条件构造sql并进行es查询解析返回
     *
     * @param eventType
     * @param uniqueName
     * @param time
     * @return
     */
    public StatisticsDto realtimeStatistics(String eventType, String uniqueName, String time) {
        String sql = Constants.EMPTY_STR;
        if (uniqueName.equals(Constants.ALL)) {
            sql = this.realtimeAllTemplate.replace(EsSqlTemplate.EVENTTYPE, eventType).replace(EsSqlTemplate.TIME,
                    time);
        } else {
            sql = this.realtimeTemplate.replace(EsSqlTemplate.EVENTTYPE, eventType).replace(EsSqlTemplate.UNIQUENAME,
                    uniqueName).replace(EsSqlTemplate.TIME, time);
        }
        StatisticsDto dto = this.parseRealtimeData(this.query(sql));
        dto.setTime(time);
        return dto;
    }

    /**
     * 根据es sql返回的结果进行解析
     *
     * @param response
     * @return
     */
    private StatisticsDto parseRealtimeData(String response) {
        if (null == response) {
            // 错误
            return null;
        } else {
            JSONObject json = JSON.parseObject(response);
            JSONArray buckets = json.getJSONObject("aggregations").getJSONObject("status").getJSONArray("buckets");
            StatisticsDto dto = new StatisticsDto();
            return this.parseStatisticsDto(buckets.iterator(), dto);
        }
    }

    /**
     * 解析StatisticsDto
     *
     * @param iterator
     * @param dto
     * @return
     */
    private StatisticsDto parseStatisticsDto(Iterator<Object> iterator, StatisticsDto dto) {
        while (iterator.hasNext()) {
            JSONObject jsonObject = ((JSONObject) iterator.next());
            if (jsonObject.getString("key").equals(EventLog.MONITOR_STATUS_FAILED)) {
                dto.setFail(jsonObject.getIntValue("doc_count"));
            } else {
                dto.setSucc(jsonObject.getIntValue("doc_count"));
            }
        }
        return dto;
    }

    public String getRealtimeTemplate() {
        return realtimeTemplate;
    }

    public void setRealtimeTemplate(String realtimeTemplate) {
        this.realtimeTemplate = realtimeTemplate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRealtimeAllTemplate() {
        return realtimeAllTemplate;
    }

    public void setRealtimeAllTemplate(String realtimeAllTemplate) {
        this.realtimeAllTemplate = realtimeAllTemplate;
    }

    public String getOfflineTemplate() {
        return offlineTemplate;
    }

    public void setOfflineTemplate(String offlineTemplate) {
        this.offlineTemplate = offlineTemplate;
    }

    public String getOfflineAllTemplate() {
        return offlineAllTemplate;
    }

    public void setOfflineAllTemplate(String offlineAllTemplate) {
        this.offlineAllTemplate = offlineAllTemplate;
    }

    public String getSearchTemplate() {
        return searchTemplate;
    }

    public void setSearchTemplate(String searchTemplate) {
        this.searchTemplate = searchTemplate;
    }

    public String getSearchAllTemplate() {
        return searchAllTemplate;
    }

    public void setSearchAllTemplate(String searchAllTemplate) {
        this.searchAllTemplate = searchAllTemplate;
    }
}
