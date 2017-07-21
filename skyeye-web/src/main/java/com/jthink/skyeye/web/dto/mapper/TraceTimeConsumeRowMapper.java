package com.jthink.skyeye.web.dto.mapper;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.data.hbase.api.RowMapper;
import com.jthink.skyeye.web.dto.TraceTimeConsumeDto;
import com.jthink.skyeye.web.util.RadixUtil;
import org.apache.hadoop.hbase.client.Result;

import java.util.Map;
import java.util.Set;

public class TraceTimeConsumeRowMapper implements RowMapper<TraceTimeConsumeDto> {

    @Override
    public TraceTimeConsumeDto mapRow(Result res, int rowNum) throws Exception {
        TraceTimeConsumeDto dto = new TraceTimeConsumeDto();
        Map<byte[], byte[]> familyMap = res.getFamilyMap(Constants.TABLE_TIME_CONSUME_COLUMN_FAMILY.getBytes());
        Set<Map.Entry<byte[], byte[]>> entrySet = familyMap.entrySet();
        for (Map.Entry<byte[], byte[]> en : entrySet) {
            dto.setTraceId(new String(en.getKey())).setConsumeTime(RadixUtil.bytesToLong(en.getValue()));
        }
        String[] ss = new String(res.getRow()).split(Constants.UNDER_LINE);
        String iface = ss[0];
        String method = ss[1];
        Long startTime = Long.parseLong(ss[2]);
        Long endTime = startTime + dto.getConsumeTime();
        String rowKey = new String(res.getRow());

        dto.setIface(iface).setMethod(method).setStartTime(startTime).setEndTime(endTime).setRowKey(rowKey);
        return dto;
    }
}