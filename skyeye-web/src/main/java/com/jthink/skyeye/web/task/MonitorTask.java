package com.jthink.skyeye.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.constant.EventType;
import com.jthink.skyeye.base.dto.AlertDto;
import com.jthink.skyeye.base.util.DateUtil;
import com.jthink.skyeye.data.http.HttpRequest;
import com.jthink.skyeye.data.jpa.domain.MonitorTemplate;
import com.jthink.skyeye.data.jpa.domain.NameInfo;
import com.jthink.skyeye.data.jpa.dto.NameInfoDto;
import com.jthink.skyeye.data.jpa.repository.MonitorTemplateRepository;
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

import java.util.*;

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
    @Autowired
    private MonitorTemplateRepository monitorTemplateRepository;

    private String mail;
    private String url;
    private String totalTemplate;
    private String template;
    private int delay;

    /**
     * 根据传递的sql语句查询es
     * @param sql
     * @return
     */
    private String query(String sql) {
        return HttpRequest.get(this.url, sql);
    }

    /**
     * 监控, 对加入监控模板的第三方、中间件、api进行报警
     */
    @Scheduled(cron = "${spring.monitor.es.interval}")
    private void monitor() {
        LOGGER.info("开始进行监控");
        StopWatch sw = new StopWatch();
        sw.start();

        String scope = "uniqueName";
        long timestamp = System.currentTimeMillis();

        // 查询出所有当前以及存在的报警模板
        List<MonitorTemplate> templates = this.monitorTemplateRepository.findAll();
        for (MonitorTemplate template : templates) {
            // 对每个模板里面包含的具体内容进行报警
            Set<String> names = this.transform(this.nameInfoRepository.findByTid(template.getId()));

            String cost = template.getCost();
            double threshold = template.getThreshold();
            int window = template.getWindow();

            // 获取当前的数据，超过响应时间的和不超过的，方便计算占比
            Map<String, Integer> totalInfos = this.parseRealtimeData(this.query(this.buildSql(timestamp, window)), scope);
            Map<String, Integer> infos = this.parseRealtimeData(this.query(this.buildSql(timestamp, cost , window)), scope);

            // 计算阈值进行报警
            for (Map.Entry<String, Integer> entry : infos.entrySet()) {
                String uniqueName = entry.getKey();
                if (names.contains(uniqueName)) {
                    // 如果当前数据存在于使用该模板的uniqueName
                    int cnt = entry.getValue();
                    double th = (double) cnt / totalInfos.get(uniqueName);
                    if (th > threshold) {
                        // 超过阈值，需要报警
                        LOGGER.info("{} 需要报警", uniqueName);
                        // 如果是api的需要在app上加一个
                        List<NameInfoDto> apis = this.nameInfoRepository.findBySql(Constants.API, uniqueName);
                        String app = uniqueName;
                        if (apis.size() != 0) {
                            app = apis.get(0).getApp() + Constants.JING_HAO + uniqueName;
                        }
                        this.rabbitmqService.sendMessage(this.buildMsg(uniqueName, window, cost, threshold, th, totalInfos.get(uniqueName), timestamp, app), this.mail);
                    }
                }
            }
        }

        sw.stop();
        LOGGER.info("结束监控, 耗时: {}ms", sw.getTotalTimeMillis());
    }

    /**
     * 构造sql
     * @param timestamp
     * @param responseTime
     * @param window
     * @return
     */
    private String buildSql(long timestamp, String responseTime, int window) {
        String sql = this.template.replace(EsSqlTemplate.BEGIN, this.getBegin(timestamp, window)).replace(EsSqlTemplate.END, this.getEnd(timestamp))
                .replace(EsSqlTemplate.COST, responseTime);
        return sql;
    }

    /**
     * 构造sql
     * @param timestamp
     * @param window
     * @return
     */
    private String buildSql(long timestamp, int window) {
        String sql = this.totalTemplate.replace(EsSqlTemplate.BEGIN, this.getBegin(timestamp, window)).replace(EsSqlTemplate.END, this.getEnd(timestamp));
        return sql;
    }

    /**
     * 构造dto以及msg
     * @param uniqueName
     * @param window
     * @param cost
     * @param threshold
     * @param total
     * @param cnt
     * @param timestamp
     * @param app
     * @return
     */
    private String buildMsg(String uniqueName, int window, String cost, double threshold, double total, int cnt, long timestamp, String app) {
        String msg = Constants.TIME_CONSUME_ALARM_TEMPLATE.replace(Constants.TIME_CONSUME_ALARM_TEMPLATE_UNIQUENAME, uniqueName)
                .replace(Constants.TIME_CONSUME_ALARM_TEMPLATE_WINDOW, String.valueOf(window))
                .replace(Constants.TIME_CONSUME_ALARM_TEMPLATE_COST, cost)
                .replace(Constants.TIME_CONSUME_ALARM_TEMPLATE_THRESHOLD, String.valueOf(threshold * 100))
                .replace(Constants.TIME_CONSUME_ALARM_TEMPLATE_TOTAL, String.valueOf(total * 100))
                .replace(Constants.TIME_CONSUME_ALARM_TEMPLATE_CNT, String.valueOf(cnt));
        AlertDto alertDto = new AlertDto();
        alertDto.setApp(app);
        alertDto.setMsg(msg);
        alertDto.setTime(new DateTime(timestamp).toString(DateUtil.YYYYMMDDHHMMSS));
        return alertDto.toString();
    }

    /**
     * 对List进行转换
     * @param nameInfos
     * @return
     */
    private Set<String> transform(List<NameInfo> nameInfos) {
        Set<String> names = new HashSet<>();
        for (NameInfo nameInfo : nameInfos) {
            names.add(nameInfo.getNameInfoPK().getName());
        }
        return names;
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
     * @param window
     * @return
     */
    private String getBegin(long timestamp, int window) {
        DateTime end = new DateTime(timestamp).minusSeconds(this.delay);
        DateTime begin = end.minusMinutes(window);
        return begin.toString(DateUtil.YYYYMMDDHHMMSS);
    }

    public String getMail() {
        return mail;
    }

    public MonitorTask setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MonitorTask setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTotalTemplate() {
        return totalTemplate;
    }

    public MonitorTask setTotalTemplate(String totalTemplate) {
        this.totalTemplate = totalTemplate;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    public MonitorTask setTemplate(String template) {
        this.template = template;
        return this;
    }

    public int getDelay() {
        return delay;
    }

    public MonitorTask setDelay(int delay) {
        this.delay = delay;
        return this;
    }
}
