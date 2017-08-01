package com.jthink.skyeye.client.logback.encoder;

import ch.qos.logback.core.Layout;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

import java.nio.charset.Charset;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc kafka的encoder
 * @date 2016-09-09 16:51:18
 */
public class KafkaLayoutEncoder<E> extends ContextAwareBase implements LifeCycle {

    // layout
    private Layout<E> layout;
    // 编码，默认utf-8
    private Charset charset;
    private boolean started = false;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public String doEncode(E event) {
        return this.layout.doLayout(event);
    }

    @Override
    public void start() {
        if (charset == null) {
            addInfo("no set charset, set the default charset is utf-8");
            charset = UTF8;
        }
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    public Layout<E> getLayout() {
        return layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
