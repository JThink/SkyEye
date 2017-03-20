package com.jthink.skyeye.web.message;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 返回消息的默认实现
 * @date 2016-10-09 09:15:34
 */
public abstract class Message<T> {

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected String resCode;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected String resMsg;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected String statCode;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    protected String statMsg;
//    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//    protected String sign;

    public Message() {
        super();
    }

    public Message(MessageCode messageCode, StatusCode statusCode) {
        this(messageCode.getCode(), messageCode.getMsg(), statusCode.getCode(), statusCode.getMsg());
    }

    public Message(String resCode, String resMsg, String staCode, String staMsg) {
        super();
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.statCode = staCode;
        this.statMsg = staMsg;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getStatCode() {
        return statCode;
    }

    public void setStatCode(String statCode) {
        this.statCode = statCode;
    }

    public String getStatMsg() {
        return statMsg;
    }

    public void setStatMsg(String statMsg) {
        this.statMsg = statMsg;
    }

//    public String getSign() {
//        return sign;
//    }
//
//    public void setSign(String sign) {
//        this.sign = sign;
//    }

    public abstract T getData();

    public abstract void setData(T data);
}
