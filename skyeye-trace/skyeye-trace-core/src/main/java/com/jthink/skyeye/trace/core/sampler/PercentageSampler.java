package com.jthink.skyeye.trace.core.sampler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 百分比采样率实现
 * 0-100条/s: 全部采集
 * 101-500条/s: 50%采集
 * 501条以上/s: 10%采集
 * @date 2017-02-15 09:57:39
 */
public class PercentageSampler implements Sampler {

    private AtomicLong count = new AtomicLong();
    private int levelOne = 100;
    private int levelTwo = 500;
    private Long lastTime = -1L;

    @Override
    public boolean isCollect() {
        boolean isSample = true;
        long n = count.incrementAndGet();
        if (System.currentTimeMillis() - lastTime  < 1000) {
            if (n > levelOne && n <= levelTwo) {
                n = n % 2;
                if (n != 0) {
                    isSample = false;
                }
            }
            if (n > levelTwo) {
                n = n % 10;
                if (n != 0) {
                    isSample = false;
                }
            }
        } else {
            count.getAndSet(0);
            lastTime = System.currentTimeMillis();
        }
        return isSample;
    }

    public static void main(String[] args) {
        PercentageSampler sampler = new PercentageSampler();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; ++i) {
            System.out.println(String.valueOf(i) + sampler.isCollect());
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
