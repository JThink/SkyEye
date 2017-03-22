package com.jthink.skyeye.benchmark.dubbo.service.a.controller;

import com.jthink.skyeye.benchmark.dubbo.service.a.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2016-12-15 15:05:06
 */
@RestController
@RequestMapping("trace")
public class TraceController {

    @Autowired
    private TraceService traceService;

    @RequestMapping(path = "trace", method = RequestMethod.GET)
    public String trace() {
        return this.traceService.trace();
    }
}
