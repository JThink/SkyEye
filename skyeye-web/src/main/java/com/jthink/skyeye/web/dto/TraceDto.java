package com.jthink.skyeye.web.dto;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.data.hbase.api.RowMapper;
import org.apache.hadoop.hbase.client.Result;

import java.io.Serializable;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Aiur
 * @version 0.0.1
 * @desc 对应 HBase trace 表
 * @date 2017-03-30 15:12:06
 */
public class TraceDto implements Serializable {

    private String traceId;

    private Set<Map.Entry<String, JSONObject>> spans;

    public String getTraceId() {
        return traceId;
    }

    public TraceDto setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public Set<Map.Entry<String, JSONObject>> getSpans() {
        return spans;
    }

    public TraceDto setSpans(Set<Map.Entry<String, JSONObject>> spans) {
        this.spans = spans;
        return this;
    }

    @Override
    public String toString() {

        return traceId + "\t" +
                spans;
    }

    public static class TraceInfoRowMapper implements RowMapper<TraceDto> {

        @Override
        public TraceDto mapRow(Result res, int rowNum) throws Exception {

            String traceId = new String(res.getRow());
            NavigableMap<byte[], byte[]> data = res.getFamilyMap(Constants.TABLE_TRACE_COLUMN_FAMILY.getBytes());

            String spanId;
            JSONObject spanDetail;
            TreeMap<String, JSONObject> map = new TreeMap<>();
            Set<Map.Entry<byte[], byte[]>> spanEntrySet = data.entrySet();
            for (Map.Entry<byte[], byte[]> entry : spanEntrySet) {
                spanId = new String(entry.getKey());
                spanDetail = JSON.parseObject(new String(entry.getValue()));
                map.put(spanId, spanDetail);
            }
            Set<Map.Entry<String, JSONObject>> spans = map.entrySet();


            TraceDto rtn = new TraceDto();
            rtn.setTraceId(traceId).setSpans(spans);
            return rtn;
        }
    }
}
