package com.jthink.skyeye.base.util;

import com.jthink.skyeye.base.constant.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 时间相关的util
 * @date 2016-09-26 10:39:45
 */
public class DateUtil {

    public final static String YYYYMMDD = "yyyy-MM-dd";
    public final static String YYYYMMDDHHmmss = "yyyyMMddHHmmss";
    public final static String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYYMMDDHHMMSSSSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 根据传入的Date和转换格式对日期进行格式化，并返回字符串表式形式
     *
     * @param date
     * @param format
     * @return
     */
    public static String parse(Date date, String format) {
        if (date == null) {
            return Constants.EMPTY_STR;
        }

        if (format == null || format.equals(Constants.EMPTY_STR)) {
            format = YYYYMMDD;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date).toString();
        } catch (Exception ex) {

            return "";
        }
    }

    /**
     * date转String
     * @param date
     * @param format
     * @return
     */
    public static String format(Date date, String format) {
        try {
            return parse(date, format);
        } catch (Exception ex) {
            return Constants.EMPTY_STR;
        }
    }

    /**
     * 根据给定的day和time返回时间（2016-11-23 16:42:40）
     * @param day
     * @param time
     * @return
     */
    public static String getTime(String day, String time) {
        return day + Constants.SPACE + time.split(Constants.POINT)[0];
    }

    /**
     * 返回月的第几周
     * @param day
     * @return
     */
    public static String getWeek(String day) {
        String[] ymd = day.split(Constants.MIDDLE_LINE);
        SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
        Date date = null;
        try {
            date = sdf.parse(day);
        } catch (ParseException e) {
            return Constants.EMPTY_STR;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return ymd[0] + Constants.MIDDLE_LINE + ymd[1] + Constants.COLON + weekOfMonth;
    }

    /**
     * 得到当前日期和时间的字符串表示形式
     *
     * @return
     */
    public static String getDateTimeStr() {
        return parse(new Date(), YYYYMMDDHHMMSS);
    }
}
