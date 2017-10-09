package com.jthink.skyeye.benchmark.log.generater.util;

import java.util.Random;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc
 * @date 2017-04-19 10:47:51
 */
public class RandomSeed {

    public static int nextInt(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; ++i) {
            System.out.println(nextInt(2));
        }
    }
}
