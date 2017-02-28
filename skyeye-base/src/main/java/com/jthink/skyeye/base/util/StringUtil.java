package com.jthink.skyeye.base.util;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 字符串相关的util
 * @date 2016-09-08 20:29:20
 */
public class StringUtil {

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !StringUtil.isBlank(str);
    }
}
