package com.jthink.skyeye.web.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.web.message.BaseMessage;
import com.jthink.skyeye.web.message.MessageCode;
import com.jthink.skyeye.web.message.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 接口返回结果工具类
 * @date 2016-10-09 09:19:47
 */
public class ResponseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtil.class);
    public static boolean IS_SIGN = true;

    /**
     * 构造返回message
     *
     * @param msg
     * @param res
     * @param sta
     */
    public static void buildResMsg(BaseMessage msg, MessageCode res, StatusCode sta) {
        if (null != msg) {
            msg.setResCode(res.getCode());
            msg.setResMsg(res.getMsg());
            msg.setStatCode(sta.getCode());
            msg.setStatMsg(sta.getMsg());
        }
    }

    /**
     * 构造返回message
     *
     * @param msg
     * @param res
     * @param sta
     * @param appendMessage
     */
    public static void buildResMsg(BaseMessage msg, MessageCode res, StatusCode sta, String appendMessage) {
        if (null != msg) {
            msg.setResCode(res.getCode());
            msg.setResMsg(res.getMsg());
            msg.setStatCode(sta.getCode());
            msg.setStatMsg(sta.getMsg() + Constants.COMMA + appendMessage);
        }
    }


    /**
     * 格式化日志记录数据
     *
     * @param logData
     * @return
     */
    public static Object formatESLogData(String logData) {
        JSONObject rtn = new JSONObject();

        JSONObject json = JSON.parseObject(logData);
        JSONObject hits = json.getJSONObject("hits");
        Iterator<Object> iterator = hits.getJSONArray("hits").iterator();

        Integer count = 0;
        List<String> logs = new ArrayList<>();
        StringBuffer sb;
        JSONObject log;
        while (iterator.hasNext()) {
            sb = new StringBuffer();
            log = ((JSONObject) iterator.next()).getJSONObject("_source");

            if(!isEmpty(log.getString("day")) && !isEmpty(log.getString("time"))){
                sb = sb.append(log.getString("day")).append(" ").append(log.getString("time"))
                        .append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("day"))){
                sb = sb.append(log.getString("day")).append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("time"))){
                sb = sb.append(log.getString("time")).append(Constants.SEMICOLON);
            }

            if(!isEmpty(log.getString("app"))){
                sb.append(log.getString("app")).append(Constants.SEMICOLON);
            }
            if(!isEmpty(log.getString("host"))){
                sb.append(log.getString("host")).append(Constants.SEMICOLON);
            }
            if(!isEmpty(log.getString("thread"))){
                sb.append(log.getString("thread")).append(Constants.SEMICOLON);
            }
            if(!isEmpty(log.getString("level"))){
                sb.append(log.getString("level")).append(Constants.SEMICOLON);
            }

            if(!isEmpty(log.getString("pack")) && !isEmpty(log.getString("clazz")) && !isEmpty(log.getString("line"))){
                sb.append(log.getString("pack")).append(Constants.POINT)
                  .append(log.getString("clazz")).append(":")
                  .append(log.getString("line")).append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("pack")) && !isEmpty(log.getString("clazz"))){
                sb.append(log.getString("pack")).append(Constants.POINT).append(log.getString("clazz"))
                  .append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("clazz")) && !isEmpty(log.getString("line"))){
                sb.append(log.getString("clazz")).append(":")
                  .append(log.getString("line")).append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("pack"))){
                sb.append(log.getString("pack")).append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("clazz"))){
                sb.append(log.getString("clazz")).append(Constants.SEMICOLON);
            }else if(!isEmpty(log.getString("line"))){
                sb.append(log.getString("line")).append(Constants.SEMICOLON);
            }

            if(!isEmpty(log.getString("message_max"))){
                sb.append(log.getString("message_max")).append(Constants.SEMICOLON);
            }

            logs.add(sb.toString());
            count++;
        }

        rtn.put("total", hits.getString("total"));
        rtn.put("resCount", count);
        rtn.put("logs", hits.getJSONArray("hits"));
        return rtn;
    }

    /**
     * 通过code获取制定枚举
     *
     * @param code
     * @return
     */
    public static StatusCode getStatusCode(String code) {
        StatusCode[] statusCodes = StatusCode.values();
        for (StatusCode status : statusCodes) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 生成返回验签
     * @param msg
     * @param privateKey
     * @return
     */
//    public static String getResponseSign(BaseMessage msg, String privateKey) {
//        List<String> keys = new ArrayList<>();
//        Map<String, String> map = new HashMap<String, String>();
//
//        // 返回必填项
//        keys.add(Constants.RES_CODE);
//        map.put(Constants.RES_CODE, msg.getResCode());
//        keys.add(Constants.RES_MSG);
//        map.put(Constants.RES_MSG,msg.getResMsg());
//        keys.add(Constants.STAT_CODE);
//        map.put(Constants.STAT_CODE, msg.getStatCode());
//        keys.add(Constants.STAT_MSG);
//        map.put(Constants.STAT_MSG,msg.getStatMsg());
//
//        if (null != msg.getData()) {
//            keys.add(Constants.DATA);
//            map.put(Constants.DATA, JSONUtil.toJson(msg.getData()));
//        }
//
//        Collections.sort(keys);
//
//        StringBuilder buf = new StringBuilder();
//        for (String key : keys) {
//            if (IS_SIGN) {
//                buf.append(key);
//            }
//            buf.append(getValue(map, key, GlobalConstants.EMPTY_STR));
//        }
//        buf.append(privateKey);
//        return SignUtil.md5Upper(buf.toString());
//    }

    /**
     *
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
//    private static String getValue(Map<String, String> map, String key, String defaultValue) {
//        String value = map.get(key);
//        return StringUtil.isBlank(value) ? defaultValue : value;
//    }


    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

}
