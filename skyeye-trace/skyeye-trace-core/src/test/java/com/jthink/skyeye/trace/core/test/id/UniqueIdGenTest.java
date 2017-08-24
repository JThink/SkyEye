package com.jthink.skyeye.trace.core.test.id;

import com.jthink.skyeye.trace.core.generater.UniqueIdGen;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 分布式唯一id生成测试
 * @date 2017-03-29 10:21:20
 */
public class UniqueIdGenTest {


    public static void main(String[] args) {
        testTimeConsume();
    }

    public static void testTimeConsume() {
        UniqueIdGen idGen = UniqueIdGen.getInstance(5);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1024000; ++i) {
            System.out.println(idGen.nextId());
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - start));
    }

}
