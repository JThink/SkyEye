package com.jthink.skyeye.web.util;

/**
 * 进制相关工具类
 * @author Aiur
 * @version 0.0.1
 * @date 2017-3-30 15:15:06
 */
public class RadixUtil {

    public static Long bytesToLong(byte[] bytes) {
        String zero = "00000000";
        StringBuilder num = new StringBuilder();
        for (byte b : bytes) {
            String binary = zero + Integer.toBinaryString(b);
            binary = binary.substring(binary.length() - 4);
            num.append(binary);
        }
        return Long.parseLong(num.toString(), 2);
    }
}
