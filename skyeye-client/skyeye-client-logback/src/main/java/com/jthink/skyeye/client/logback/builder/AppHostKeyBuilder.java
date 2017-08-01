package com.jthink.skyeye.client.logback.builder;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;

import java.nio.ByteBuffer;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 根据app名字和host值生成key（如果kafka有多个partition，由于kafka的限制，每个partition只能由相等数量的每个cosumer group
 *          中的consumer进行消费，并且consumer group中只能有一个consumer消费，为了保证后续logstash进行消费能够保证每个应用的日志
 *          有序，key加上app，相同的app进入相同的partition，由于app有可能是部署多个节点，所以key在加上host可以保证每个app在不同的
 *          节点上的日志能够有序得进行消费）
 * @date 2016-09-09 13:27:35
 */
public class AppHostKeyBuilder<E> extends ContextAwareBase implements KeyBuilder<E> {

    private byte[] appHost;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        String host = context.getProperty(CoreConstants.HOSTNAME_KEY);
        String app = context.getName();
        appHost = ByteBuffer.allocate(4).putInt(new StringBuilder(app).append(host).toString().hashCode()).array();
    }

    /**
     * 生成key，key规则app+host的byte[]
     * @param e log event, ch.qos.logback.classic.spi.ILoggingEvent
     * @return
     */
    @Override
    public byte[] build(E e) {
        return appHost;
    }

    public byte[] getAppHost() {
        return appHost;
    }

    public void setAppHost(byte[] appHost) {
        this.appHost = appHost;
    }
}
