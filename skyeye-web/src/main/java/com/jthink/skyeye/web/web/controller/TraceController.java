package com.jthink.skyeye.web.web.controller;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.data.jpa.repository.ServiceInfoRepository;
import com.jthink.skyeye.web.dto.TraceAnnotationDto;
import com.jthink.skyeye.web.dto.TraceDto;
import com.jthink.skyeye.web.dto.TraceStatisticsDto;
import com.jthink.skyeye.web.dto.TraceTimeConsumeDto;
import com.jthink.skyeye.web.dto.mapper.TraceStatisticsMapper;
import com.jthink.skyeye.web.message.BaseMessage;
import com.jthink.skyeye.web.message.MessageCode;
import com.jthink.skyeye.web.message.StatusCode;
import com.jthink.skyeye.web.service.TraceService;
import com.jthink.skyeye.web.util.ResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc rpc trace 相关的 controller
 * @date 2017-03-31 20:22:08
 */
@RestController
@RequestMapping("rpctrace")
public class TraceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceController.class);
    @Autowired
    private TraceService traceService;

    @Autowired
    private ServiceInfoRepository serviceInfoRepository;

    public static final String NEW_LAST_ROW = "newLastRow";
    public static final String TRACE_INFOS = "traceInfos";


    /**
     * 获取服务信息
     *
     * @return serviceInfo
     */
    @RequestMapping(path = "serviceInfo", method = RequestMethod.GET)
    public BaseMessage serviceInfo() {

        BaseMessage msg = new BaseMessage();
        try {
            msg.setData(traceService.getServiceInfo());
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取 serviceInfo 数据失败。");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    /**
     * 根据设定的条件查询符合要求的追踪链的信息。
     *
     * @param sid       sid, 即 iface_method
     * @param type      异常类型
     * @param startTime 起始时间范围。
     * @param endTime   结束时间范围。
     * @param lastRow   上次请求的最后一行，用于分页。
     * @param pageSize  单页条数。
     * @return
     */
    @RequestMapping(path = "traceInfoList", method = RequestMethod.GET)
    public BaseMessage traceInfoList(@RequestParam(value = "sid", required = false) final String sid,
                                     @RequestParam(value = "type", required = false) final String type,
                                     @RequestParam(value = "startTime", required = false) final Long startTime,
                                     @RequestParam(value = "endTime", required = false) final Long endTime,
                                     @RequestParam(value = "lastRow", required = false) final String lastRow,
                                     @RequestParam(value = "pageSize", required = false) final Integer pageSize) {
        BaseMessage msg = new BaseMessage();
        try {
            ArrayList<String> rows = new ArrayList<>();
            String newLastRow = "";

            // 若异常类型为空，从 time_consume 表获取 traceId。否则通过 annotation 表获取 traceId。
            if (StringUtils.isEmpty(type)) {
                List<TraceTimeConsumeDto> traceTimeConsumes = traceService.getTraceTimeConsumes(sid, startTime,
                        endTime, lastRow, pageSize);
                for (TraceTimeConsumeDto trc : traceTimeConsumes) {
                    rows.add(trc.getTraceId());
                    newLastRow = trc.getRowKey();
                }
            } else {
                List<TraceAnnotationDto> list = traceService.getAnnotations(sid, type, startTime, endTime,
                        lastRow, pageSize);
                for (TraceAnnotationDto tad : list) {
                    rows.add(tad.getTraceId());
                    newLastRow = tad.getRowKey();
                }
            }

            List<TraceDto> traceInfos = traceService.getTraceInfos(rows);
            if (pageSize > traceInfos.size()) {
                newLastRow = null;
            }

            Map<String, Object> data = new HashMap<String, Object>();
            data.put(NEW_LAST_ROW, newLastRow);
            data.put(TRACE_INFOS, traceInfos);
            msg.setData(data);
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取追踪链信息失败。");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }


    /**
     * 根据设定的条件查询符合要求的追踪链总耗时情况。
     *
     * @param sid       sid, 即 iface_method
     * @param startTime 起始时间范围。
     * @param endTime   结束时间范围。
     * @param lastRow   上次请求的最后一行，用于分页。
     * @param pageSize  单页条数。
     * @return
     */
    @RequestMapping(path = "timeConsume", method = RequestMethod.GET)
    public BaseMessage timeConsume(@RequestParam(value = "sid", required = false) final String sid,
                                   @RequestParam(value = "startTime", required = false) final Long startTime,
                                   @RequestParam(value = "endTime", required = false) final Long endTime,
                                   @RequestParam(value = "lastRow", required = false) final String lastRow,
                                   @RequestParam(value = "pageSize", required = false) final Integer pageSize) {
        BaseMessage msg = new BaseMessage();
        try {
            msg.setData(traceService.getTraceTimeConsumes(sid, startTime, endTime, lastRow, pageSize));
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("查询追踪链总耗时数据失败。");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }


    /**
     * 根据设定的条件查询符合要求的 Annotation 信息。
     *
     * @param sid       sid, 即 iface_method
     * @param type      异常类型
     * @param startTime 起始时间范围。
     * @param endTime   结束时间范围。
     * @param lastRow   上次请求的最后一行，用于分页。
     * @param pageSize  单页条数。
     * @return responseBody
     */
    @RequestMapping(path = "annotation", method = RequestMethod.GET)
    public BaseMessage annotation(@RequestParam(value = "sid", required = false) final String sid,
                                  @RequestParam(value = "type", required = false) final String type,
                                  @RequestParam(value = "startTime", required = false) final Long startTime,
                                  @RequestParam(value = "endTime", required = false) final Long endTime,
                                  @RequestParam(value = "lastRow", required = false) final String lastRow,
                                  @RequestParam(value = "pageSize", required = false) final Integer pageSize) {
        BaseMessage msg = new BaseMessage();
        try {
            msg.setData(traceService.getAnnotations(sid, type, startTime, endTime, lastRow, pageSize));
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("查询 Annotation 数据失败。");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    /**
     * 获取对应 TraceId 的完整追踪信息。
     *
     * @param traceId
     * @return
     */
    @RequestMapping(path = "traceInfo", method = RequestMethod.GET)
    public BaseMessage traceInfo(@RequestParam(value = "traceId", required = false) final String traceId) {

        BaseMessage msg = new BaseMessage();
        try {
            msg.setData(traceService.getTraceInfo(traceId));
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取 TraceInfo 数据失败。TraceId = " + traceId);
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    /**
     * 查询接口名称
     *
     * @return
     */
    @RequestMapping(path = "names", method = RequestMethod.GET)
    public BaseMessage names() {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(serviceInfoRepository.findAllIface());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("查询服务接口名称失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    /**
     * 根据接口名称查询方法名
     *
     * @param iface
     * @return
     */
    @RequestMapping(path = "methods", method = RequestMethod.GET)
    public BaseMessage methods(@RequestParam(value = "iface", required = false) final String iface) {
        BaseMessage msg = new BaseMessage();
        try {
            ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
            msg.setData(serviceInfoRepository.findMethodByIface(iface));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("查询方法名称失败");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }

    /**
     * 获取某一接口下的某一方法下范围时间内的统计结果
     *
     * @param serviceName
     * @param methodName
     * @param begin
     * @param end
     * @param scope
     * @return
     */
    @RequestMapping(path = "trace", method = RequestMethod.GET)
    public BaseMessage rpcTrace(@RequestParam(value = "uniqueIface", required = false) final String serviceName,
                                @RequestParam(value = "uniqueMethod", required = false) final String methodName,
                                @RequestParam(value = "begin", required = false) final String begin,
                                @RequestParam(value = "end", required = false) final String end,
                                @RequestParam(value = "scope", required = false) final String scope) {
        BaseMessage msg = new BaseMessage();
        ResponseUtil.buildResMsg(msg, MessageCode.SUCCESS, StatusCode.SUCCESS);
        try {
            List<TraceStatisticsDto> rpcTraces = traceService.getDatas(Constants.TRACE_TABLE_NAME, serviceName, methodName, begin, end, scope, new TraceStatisticsMapper());
            msg.setData(rpcTraces);
            return msg;
        } catch (Exception e) {
            LOGGER.error("rpc服务调用查询失败。");
            ResponseUtil.buildResMsg(msg, MessageCode.FAILED, StatusCode.SYSTEM_ERROR);
        }
        return msg;
    }


}