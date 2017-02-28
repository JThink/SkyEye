package com.jthink.skyeye.base.constant;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 日志级别
 * @date 2016-10-12 21:33:18
 */
public enum LogLevel {

    INFO(Constants.LOG_LEVEL_INFO),
    ERROR(Constants.LOG_LEVEL_ERROR),
    WARNING(Constants.LOG_LEVEL_WARNING);

    private String label;

    private LogLevel(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }

    /**
     *
     * @param level
     * @return
     */
    public boolean isLegal(String level) {
        try {
            LogLevel.valueOf(level);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
