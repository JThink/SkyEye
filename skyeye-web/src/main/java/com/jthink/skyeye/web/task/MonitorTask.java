package com.jthink.skyeye.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.dto.AlertDto;
import com.jthink.skyeye.base.util.DateUtil;
import com.jthink.skyeye.data.http.HttpRequest;
import com.jthink.skyeye.data.jpa.dto.NameInfoDto;
import com.jthink.skyeye.data.jpa.repository.NameInfoRepository;
import com.jthink.skyeye.data.rabbitmq.service.RabbitmqService;
import com.jthink.skyeye.web.constant.EsSqlTemplate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-01 17:07:37
 */
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "spring.monitor.es")
public class MonitorTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorTask.class);

    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private NameInfoRepository nameInfoRepository;

    private String mail;
    private int interval;
    private String url;
    private String middlewareResponseTime;
    private double middlewareThreshold;
    private String thirdResponseTime;
    private double thirdThreshold;
    private String totalTemplate;
    private String template;
    private String apiResponseTime;
    private double apiThreshold;
    private int delay;

    /**
     * 根据传递的sql语句查询es
     * @param sql
     * @return
     */
    private String query(String sql) {
        String response = HttpRequest.get(this.url, sql);
        return response;
    }

    /**
     * 对第三方进行监控报警
     */
    @Scheduled(cron = "${spring.monitor.es.window}")
    private void monitorThirdParty() {
        LOGGER.info("开始对第三方进行监控");
        StopWatch sw = new StopWatch();
        sw.start();

        long timestamp = System.currentTimeMillis();
        String scope = "uniqueName";

        Map<String, Integer> thirdTotalInfos = this.parseRealtimeData(this.query(this.buildSql(this.totalTemplate,
                timestamp, scope, this.thirdResponseTime, EventType.thirdparty_call)), scope);

        Map<String, Integer> thirdInfos = this.parseRealtimeData(this.query(this.buildSql(this.template, timestamp,
                scope, this.thirdResponseTime, EventType.thirdparty_call)), scope);

        for (Map.Entry<String, Integer> entry : thirdInfos.entrySet()) {
            String third = entry.getKey();
            int cnt = entry.getValue();
            double threadhold = (double) cnt / thirdTotalInfos.get(third);
            if (threadhold > this.thirdThreshold) {
                // 超过阈值，需要报警
                LOGGER.info("{} 需要报警", third);
                this.rabbitmqService.sendMessage(this.buildMsg(third, timestamp, third, this.thirdResponseTime, this.thirdThreshold, threadhold,
                        thirdTotalInfos.get(third)), this.mail);
            }
        }

        sw.stop();
        LOGGER.info("结束对第三方进行监控, 耗时: {}ms", sw.getTotalTimeMillis());
    }

    /**
     * 对中间件进行监控报警
     */
    @Scheduled(cron = "${spring.monitor.es.window}")
    private void monitorMiddleWare() {
        LOGGER.info("开始对中间件进行监控");
        StopWatch sw = new StopWatch();
        sw.start();

        long timestamp = System.currentTimeMillis();
        String scope = "uniqueName";

        Map<String, Integer> middlewareTotalInfos = this.parseRealtimeData(this.query(this.buildSql(this.totalTemplate,
                timestamp, scope, this.middlewareResponseTime, EventType.middleware_opt)), scope);

        Map<String, Integer> middlewareInfos = this.parseRealtimeData(this.query(this.buildSql(this.template, timestamp,
                scope, this.middlewareResponseTime, EventType.middleware_opt)), scope);

        for (Map.Entry<String, Integer> entry : middlewareInfos.entrySet()) {
            String middleware = entry.getKey();
            int cnt = entry.getValue();
            double threadhold = (double) cnt / middlewareTotalInfos.get(middleware);
            if (threadhold > this.middlewareThreshold) {
                // 超过阈值，需要报警
                LOGGER.info("{} 需要报警", middleware);
                this.rabbitmqService.sendMessage(this.buildMsg(middleware, timestamp, middleware, this.middlewareResponseTime, this.middlewareThreshold,
                        threadhold, middlewareTotalInfos.get(middleware)), this.mail);
            }
        }

        sw.stop();
        LOGGER.info("结束对中间件进行监控, 耗时: {}ms", sw.getTotalTimeMillis());
    }

    /**
     * 对api进行监控报警
     */
    @Scheduled(cron = "${spring.monitor.es.window}")
    private void monitorApi() {
        LOGGER.info("开始对api进行监控");
        StopWatch sw = new StopWatch();
        sw.start();
        long timestamp = System.currentTimeMillis();
        String scope = "uniqueName";

        Map<String, Integer> appTotalInfos = this.parseRealtimeData(this.query(this.buildSql(this.totalTemplate,
                timestamp, scope, this.apiResponseTime, EventType.invoke_interface)), scope);

        Map<String, Integer> apiInfos = this.parseRealtimeData(this.query(this.buildSql(this.template, timestamp,
                scope, this.apiResponseTime, EventType.invoke_interface)), scope);

        for (Map.Entry<String, Integer> entry : apiInfos.entrySet()) {
            String api = entry.getKey();
            int cnt = entry.getValue();
            double threadhold = (double) cnt / appTotalInfos.get(api);
            if (threadhold > this.apiThreshold) {
                // 超过阈值，需要报警
                LOGGER.info("{} 需要报警", api);
                List<NameInfoDto> apis =  this.nameInfoRepository.findBySql(Constants.API, api);
                String name = Constants.EMPTY_STR;
                if (apis.size() != 0) {
                    name = apis.get(0).getApp();
                }
                this.rabbitmqService.sendMessage(this.buildMsg(name + Constants.JING_HAO + api, timestamp, api, this.apiResponseTime, this.apiThreshold, threadhold,
                        appTotalInfos.get(api)), this.mail);
            }
        }
        sw.stop();
        LOGGER.info("结束对api进行监控, 耗时: {}ms", sw.getTotalTimeMillis());
    }

    /**
     * 构造dto以及msg
     * @param app
     * @param timestamp
     * @param key
     * @param responseTime
     * @param threshold
     * @param currentThreshold
     * @return
     */
    private String buildMsg(String app, long timestamp, String key, String responseTime, double threshold, double currentThreshold, int callTotalCnt) {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(Constants.WECHAT_ALERT_RESPONSE_EXCEED).append(Constants.COMMA).append(this.interval).append("分钟内响应时间超过").append(responseTime)
                .append("ms占比大于").append(threshold * 100).append("%").append(Constants.COMMA).append("当前占比:").append(currentThreshold * 100).append("%")
                .append(Constants.COMMA).append("总请求次数:").append(callTotalCnt);
        AlertDto alertDto = new AlertDto();
        alertDto.setApp(app);
        alertDto.setMsg(sb.toString());
        alertDto.setTime(new DateTime(timestamp).toString(DateUtil.YYYYMMDDHHMMSS));
        return alertDto.toString();
    }

    /**
     * 根据时间、scope和响应时间限制构造sql
     * @param template
     * @param timestamp
     * @param scope
     * @param responseTime
     * @return
     */
    private String buildSql(String template, long timestamp, String scope, String responseTime, EventType eventType) {
        String sql = template.replace(EsSqlTemplate.EVENTTYPE, eventType.symbol())
                .replace(EsSqlTemplate.BEGIN, this.getBegin(timestamp)).replace(EsSqlTemplate.END, this.getEnd(timestamp))
                .replace(EsSqlTemplate.SCOPE, scope).replace(EsSqlTemplate.COST, responseTime);
        return sql;
    }

    /**
     * 根据es sql返回的结果进行解析
     * @param response
     * @return
     */
    private Map<String, Integer> parseRealtimeData(String response, String scope) {
        Map<String , Integer> apps = new HashMap<String, Integer>();
        if (null == response) {
            // 错误
            return apps;
        } else {
            JSONObject json = JSON.parseObject(response);
            Iterator<Object> iterator = json.getJSONObject("aggregations").getJSONObject(scope).getJSONArray("buckets").iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObject = ((JSONObject)iterator.next());
                String app = jsonObject.getString("key");
                int cnt = jsonObject.getIntValue("doc_count");
                apps.put(app, cnt);
            }
            return apps;
        }
    }

    /**
     * 根据时间戳获取结束时间
     * @param timestamp
     * @return
     */
    private String getEnd(long timestamp) {
        DateTime end = new DateTime(timestamp).minusSeconds(this.delay);
        return end.toString(DateUtil.YYYYMMDDHHMMSS);
    }

    /**
     * 根据时间戳获取开始时间
     * @param timestamp
     * @return
     */
    private String getBegin(long timestamp) {
        DateTime end = new DateTime(timestamp).minusSeconds(this.delay);
        DateTime begin = end.minusMinutes(this.interval);
        return begin.toString(DateUtil.YYYYMMDDHHMMSS);
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMiddlewareResponseTime() {
        return middlewareResponseTime;
    }

    public void setMiddlewareResponseTime(String middlewareResponseTime) {
        this.middlewareResponseTime = middlewareResponseTime;
    }

    public double getMiddlewareThreshold() {
        return middlewareThreshold;
    }

    public void setMiddlewareThreshold(double middlewareThreshold) {
        this.middlewareThreshold = middlewareThreshold;
    }

    public String getThirdResponseTime() {
        return thirdResponseTime;
    }

    public void setThirdResponseTime(String thirdResponseTime) {
        this.thirdResponseTime = thirdResponseTime;
    }

    public double getThirdThreshold() {
        return thirdThreshold;
    }

    public void setThirdThreshold(double thirdThreshold) {
        this.thirdThreshold = thirdThreshold;
    }

    public String getTotalTemplate() {
        return totalTemplate;
    }

    public void setTotalTemplate(String totalTemplate) {
        this.totalTemplate = totalTemplate;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getApiResponseTime() {
        return apiResponseTime;
    }

    public void setApiResponseTime(String apiResponseTime) {
        this.apiResponseTime = apiResponseTime;
    }

    public double getApiThreshold() {
        return apiThreshold;
    }

    public void setApiThreshold(double apiThreshold) {
        this.apiThreshold = apiThreshold;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
