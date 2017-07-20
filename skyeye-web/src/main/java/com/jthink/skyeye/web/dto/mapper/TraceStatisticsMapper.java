package com.jthink.skyeye.web.dto.mapper;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.data.hbase.api.RowMapper;
import com.jthink.skyeye.web.dto.TraceStatisticsDto;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.Serializable;

public class TraceStatisticsMapper implements RowMapper<TraceStatisticsDto>, Serializable {

    @Override
    public TraceStatisticsDto mapRow(Result result, int rowNum) throws Exception {
        TraceStatisticsDto rpcTrace = new TraceStatisticsDto();
        byte[] success = result.getValue(Bytes.toBytes(Constants.TRACE_TYPE), Bytes.toBytes(Constants.TRACE_SUCCESS));
        if (null != success) {
            rpcTrace.setSuccess(new String(success));
        }
        byte[] fail = result.getValue(Bytes.toBytes(Constants.TRACE_TYPE), Bytes.toBytes(Constants.TRACE_FAIL));
        if (null != fail) {
            rpcTrace.setFail(new String(fail));
        }
        byte[] max = result.getValue(Bytes.toBytes(Constants.TRACE_TYPE), Bytes.toBytes(Constants.TRACE_MAX));
        if (null != max) {
            rpcTrace.setMax(String.valueOf(Double.valueOf(new String(max)) / 1000));
        }
        byte[] min = result.getValue(Bytes.toBytes(Constants.TRACE_TYPE), Bytes.toBytes(Constants.TRACE_MIN));
        if (null != min) {
            rpcTrace.setMin(String.valueOf(Double.valueOf(new String(min)) / 1000));
        }
        byte[] average = result.getValue(Bytes.toBytes(Constants.TRACE_TYPE), Bytes.toBytes(Constants.TRACE_AVERAGE));
        if (null != average) {
            rpcTrace.setAverage(String.valueOf(Double.valueOf(new String(average)) / 1000));
        }
        byte[] row = result.getRow();
        if (null != row) {
            rpcTrace.setRowkey(new String(row));
        }
        return rpcTrace;
    }
}
