package com.jthink.skyeye.benchmark.spring.cloud.service.a.controller;

import com.jthink.skyeye.benchmark.spring.cloud.service.a.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 测试接口
 * @date 2017-06-05 11:36:52
 */
@RestController
@RequestMapping("service-a")
public class TraceController {

    @Autowired
    private TraceService traceService;

    @RequestMapping(path = "trace/1", method = RequestMethod.GET)
    public String trace() {
        return this.traceService.trace1();
    }

    @RequestMapping(path = "trace/2", method = RequestMethod.GET)
    public String trace2() {
        return this.traceService.trace2();
    }

    @RequestMapping(path = "trace/3", method = RequestMethod.GET)
    public String trace3() {
        return this.traceService.trace3();
    }

    @RequestMapping(path = "trace/4", method = RequestMethod.GET)
    public String trace4() {
        return this.traceService.trace4();
    }

    @RequestMapping(path = "trace/5", method = RequestMethod.GET)
    public String trace5() {
        return this.traceService.trace5();
    }

    @RequestMapping(path = "trace/6", method = RequestMethod.GET)
    public String trace6() {
        return this.traceService.trace6();
    }

}
