package com.jthink.skyeye.base.dapper;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 自定义annotation
 * @date 2016-11-04 17:32:31
 */
public class BinaryAnnotation implements Serializable {

    // 自定义key
    private String key;
    // 自定义value
    private String value;
    // 类型
    private String type;
    // endpoint
    private EndPoint endPoint;

    public BinaryAnnotation() {

    }

    public BinaryAnnotation(String key, String value, String type, EndPoint endPoint) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.endPoint = endPoint;
    }

    @Override
    public String toString() {
        return "BinaryAnnotation{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", endPoint=" + endPoint +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryAnnotation that = (BinaryAnnotation) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return endPoint != null ? endPoint.equals(that.endPoint) : that.endPoint == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
        return result;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(EndPoint endPoint) {
        this.endPoint = endPoint;
    }
}
