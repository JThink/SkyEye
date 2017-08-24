package com.jthink.skyeye.trace.core.registry;

import com.jthink.skyeye.trace.core.dto.RegisterDto;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 注册中心注册器
 * @date 2017-03-23 10:10:06
 */
public interface Registry {

    /**
     * 对服务进行注册
     * @return 返回注册serviceID
     */
    String register(RegisterDto registerDto);
}
