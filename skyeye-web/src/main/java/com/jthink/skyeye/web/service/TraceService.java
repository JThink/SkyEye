package com.jthink.skyeye.web.service;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.base.util.DateUtil;
import com.jthink.skyeye.data.hbase.api.HbaseTemplate;
import com.jthink.skyeye.data.jpa.domain.ServiceInfo;
import com.jthink.skyeye.data.jpa.repository.ServiceInfoRepository;
import com.jthink.skyeye.web.dto.TraceAnnotationDto;
import com.jthink.skyeye.web.dto.TraceDto;
import com.jthink.skyeye.web.dto.TraceStatisticsDto;
import com.jthink.skyeye.web.dto.TraceTimeConsumeDto;
import com.jthink.skyeye.web.dto.mapper.TraceStatisticsMapper;
import com.jthink.skyeye.web.dto.mapper.TraceTimeConsumeRowMapper;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * rpc 追踪相关 service
 */
@Service
public class TraceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceService.class);

    @Autowired
    HbaseTemplate hBaseTemplate;

    @Autowired
    ServiceInfoRepository serviceInfoRepository;


    /**
     * 获取服务信息。
     *
     * @return Iterable<ServiceInfo>
     */
    public Iterable<ServiceInfo> getServiceInfo() {
        return serviceInfoRepository.findAll();
    }


    /**
     * 获取符合条件的追踪链的 traceId 及完整追踪链耗时。
     *
     * @param sid       sid, iface_method
     * @param startTime 起始时间范围。
     * @param endTime   结束时间范围。
     * @param lastRow   上次请求的最后一行，用于分页。
     * @param pageSize  单页条数。
     * @return List<TraceTimeConsumeDto>
     */
    public List<TraceTimeConsumeDto> getTraceTimeConsumes(String sid, Long startTime, Long endTime, String
            lastRow, int pageSize) throws IOException {

        Scan scan = new Scan();

        String min = sid + Constants.UNDER_LINE + startTime;
        String max = sid + Constants.UNDER_LINE + endTime;

        FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        list.addFilter(new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(min.getBytes())));
        list.addFilter(new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(max.getBytes())));


        list.addFilter(new PageFilter(pageSize));

        scan.setFilter(list);
        if (lastRow != null && lastRow.length() > 0) {
            byte[] startRow = Bytes.add(lastRow.getBytes(), "".getBytes());
            scan.setStartRow(startRow);
        }

        return hBaseTemplate.find(Constants.TABLE_TIME_CONSUME, scan, new TraceTimeConsumeRowMapper());
    }

    /**
     * 获取完整的追踪链信息。
     *
     * @param traceId TraceId
     * @return TraceDto
     */
    public TraceDto getTraceInfo(String traceId) {
        return hBaseTemplate.get(Constants.TABLE_TRACE, traceId, new TraceDto.TraceInfoRowMapper());
    }

    /**
     * 批量获取  TraceInfo
     *
     * @param traceIds TraceIds
     * @return List<TraceDto>
     * @throws Exception
     */
    public List<TraceDto> getTraceInfos(List<String> traceIds) throws Exception {
        Connection conn = hBaseTemplate.getConnection();
        Table table = conn.getTable(TableName.valueOf(Constants.TABLE_TRACE));
        ArrayList<Get> gets = new ArrayList<>();
        for (String traceId : traceIds) {
            gets.add(new Get(traceId.getBytes()));
        }

        ArrayList<TraceDto> list = new ArrayList<>();
        TraceDto.TraceInfoRowMapper mapper = new TraceDto.TraceInfoRowMapper();
        Result[] results = table.get(gets);
        for (Result res : results) {
            list.add(mapper.mapRow(res, 0));
        }
        return list;
    }

    /**
     * 获取符合条件的 Annotation 。
     *
     * @param sid       sid, iface_method
     * @param type      异常类型。
     * @param startTime 起始时间范围。
     * @param endTime   结束时间范围。
     * @param lastRow   上次请求的最后一行，用于分页。
     * @param pageSize  单页条数。
     * @return List<TraceTimeConsumeDto>
     */
    public List<TraceAnnotationDto> getAnnotations(String sid, String type, Long startTime, Long endTime, String
            lastRow, int pageSize) {

        Scan scan = new Scan();

        String min = sid + Constants.UNDER_LINE + type + Constants.UNDER_LINE + startTime;
        String max = sid + Constants.UNDER_LINE + type + Constants.UNDER_LINE + endTime;

        FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        list.addFilter(new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(min.getBytes())));
        list.addFilter(new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(max.getBytes())));


        list.addFilter(new PageFilter(pageSize));

        scan.setFilter(list);
        if (lastRow != null && lastRow.length() > 0) {
            byte[] startRow = Bytes.add(lastRow.getBytes(), "".getBytes());
            scan.setStartRow(startRow);
        }

        return hBaseTemplate.find(Constants.TABLE_ANNOTATION, scan, new TraceAnnotationDto.TraceAnnotationMapper());
    }


    /**
     * 根据以下参数获取数据
     *
     * @return
     */
    public List<TraceStatisticsDto> getDatas(String tableName,
                                             String serviceName,
                                             String methodName,
                                             String begin,
                                             String end,
                                             String scope,
                                             TraceStatisticsMapper mapper) throws ParseException {
        //根据条件创建scan
        Scan scan = this.buildScan(serviceName, methodName, begin, end);
        //按条件查询rpctrace
        List<TraceStatisticsDto> rpctraces = hBaseTemplate.find(tableName, scan, mapper);
        //对rpctrace结果按需返回
        Map<String, Integer> integerMap = new HashMap<String, Integer>();
        Map<String, Double> doubleMap = new HashMap<>();
        //对rpctrace结果按需返回
        for (TraceStatisticsDto rpcTrace : rpctraces) {
            String time = rpcTrace.getRowkey().split(Constants.UNDER_LINE)[2];
            if (scope.equals(Constants.DAY)) {
                rpcTrace.setTime(time);
            } else if (scope.equals(Constants.WEEK)) {
                //获取周的时间字符串
                SimpleDateFormat df = new SimpleDateFormat(DateUtil.YYYYMMDD);
                Date date = df.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                String timeWeek = time.substring(0, 7) + Constants.COLON + weekOfMonth;
                rpcTrace.setTime(timeWeek);
                //返回数据的二次统计
                this.setResult(timeWeek, rpcTrace, integerMap, doubleMap);
            } else if (scope.equals(Constants.MONTH)) {
                String timeMonth = time.substring(0, 7);
                rpcTrace.setTime(timeMonth);
                //返回数据的二次统计
                this.setResult(timeMonth, rpcTrace, integerMap, doubleMap);
            }
        }
        return rpctraces;
    }

    //根据条件创建scan
    private Scan buildScan(String serviceName, String methodName, String begin, String end) {
        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<Filter>();
        Filter filter1 = new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(serviceName + Constants.UNDER_LINE + methodName + Constants.UNDER_LINE + begin)));
        Filter filter2 = new RowFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes(serviceName + Constants.UNDER_LINE + methodName + Constants.UNDER_LINE + end)));
        filters.add(filter1);
        filters.add(filter2);
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters);
        scan.setFilter(filterList);
        return scan;
    }

    //设置返回数据
    public void setResult(String timeFormat, TraceStatisticsDto rpcTrace, Map<String, Integer> integerMap, Map<String, Double> doubleMap) {
        //成功次数以及平均值
        if (null != integerMap.get(timeFormat + Constants.TRACE_SUCCESS) && null != rpcTrace.getSuccess()) {
            integerMap.put(timeFormat + Constants.TRACE_SUCCESS, integerMap.get(timeFormat + Constants.TRACE_SUCCESS) + Integer.valueOf(rpcTrace.getSuccess()));
            doubleMap.put(timeFormat + Constants.TRACE_TOTAL, doubleMap.get(timeFormat + Constants.TRACE_TOTAL) + Integer.valueOf(rpcTrace.getSuccess()) * Double.valueOf(rpcTrace.getAverage()));
            rpcTrace.setSuccess(String.valueOf(integerMap.get(timeFormat + Constants.TRACE_SUCCESS)));
            rpcTrace.setAverage(String.valueOf(doubleMap.get(timeFormat + Constants.TRACE_TOTAL) / integerMap.get(timeFormat + Constants.TRACE_SUCCESS)));
        } else {
            integerMap.put(timeFormat + Constants.TRACE_SUCCESS, Integer.valueOf(rpcTrace.getSuccess()));
            doubleMap.put(timeFormat + Constants.TRACE_TOTAL, Integer.valueOf(rpcTrace.getSuccess()) * Double.valueOf(rpcTrace.getAverage()));
            rpcTrace.setAverage(String.valueOf(doubleMap.get(timeFormat + Constants.TRACE_TOTAL) / integerMap.get(timeFormat + Constants.TRACE_SUCCESS)));
        }

        //失败次数
        if (null != integerMap.get(timeFormat + Constants.TRACE_FAIL) && null != rpcTrace.getFail()) {
            integerMap.put(timeFormat + Constants.TRACE_FAIL, integerMap.get(timeFormat + Constants.TRACE_FAIL) + Integer.valueOf(rpcTrace.getFail()));
            rpcTrace.setFail(String.valueOf(integerMap.get(timeFormat + Constants.TRACE_FAIL)));
        } else {
            if (null != rpcTrace.getFail()) {
                integerMap.put(timeFormat + Constants.TRACE_FAIL, Integer.valueOf(rpcTrace.getFail()));
            } else {
                rpcTrace.setFail(String.valueOf(integerMap.get(timeFormat + Constants.TRACE_FAIL)));
            }
        }

        //最大值
        if (null != doubleMap.get(timeFormat + Constants.TRACE_MAX) && null != rpcTrace.getMax()) {
            Double temp = Double.valueOf(rpcTrace.getMax()) > doubleMap.get(timeFormat + Constants.TRACE_MAX) ? Double.valueOf(rpcTrace.getMax()) : doubleMap.get(timeFormat + Constants.TRACE_MAX);
            rpcTrace.setMax(String.valueOf(temp));
            doubleMap.put(timeFormat + Constants.TRACE_MAX, temp);
        } else {
            doubleMap.put(timeFormat + Constants.TRACE_MAX, Double.valueOf(rpcTrace.getMax()));
        }

        //最小值
        if (null != doubleMap.get(timeFormat + Constants.TRACE_MIN) && null != rpcTrace.getMin()) {
            Double temp = Double.valueOf(rpcTrace.getMin()) < doubleMap.get(timeFormat + Constants.TRACE_MIN) ? Double.valueOf(rpcTrace.getMin()) : doubleMap.get(timeFormat + Constants.TRACE_MIN);
            rpcTrace.setMin(String.valueOf(temp));
            doubleMap.put(timeFormat + Constants.TRACE_MIN, temp);
        } else {
            doubleMap.put(timeFormat + Constants.TRACE_MIN, Double.valueOf(rpcTrace.getMin()));
        }
    }
}
