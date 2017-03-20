package com.jthink.skyeye.web.message;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 消息码
 * @date 2016-10-09 09:15:34
 */
public enum MessageCode {

    SUCCESS("0000", "提交成功"),
    FAILED("0001", "提交失败");

    private String code;
    private String msg;

    MessageCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
