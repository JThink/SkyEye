package com.jthink.skyeye.base.sql;

import com.jthink.skyeye.base.constant.Constants;

import java.io.Serializable;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc where子句具体原子查询条件
 * @date 2016-11-29 21:10:31
 */
public class Condition implements Serializable {

    private static final long serialVersionUID = 1L;

    // 列
    private String column;
    // 列函数
    private Func func;
    // 操作
    private Opt opt;
    // 操作值
    private String value;
    // or还是and
    private String flag;

    /**
     * 验证基本的参数是否符合
     * @return
     */
    private boolean valid() {
        if (this.column == null || this.opt == null) {
            return false;
        }
        if (!this.opt.equals(Opt.IS_NULL) && this.value == null) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String str = Constants.EMPTY_STR;
        if (this.valid()) {
            if (this.func != null) {
                str = this.func.name() + "(" + this.column + ")";
            } else {
                str = this.column;
            }
            if (this.opt.symbol().equals(Opt.EQUAL.symbol())) {
                // =操作
                if (this.value != null) {
                    str += this.opt.symbol();
                    str += Constants.APOSTROPHE + this.value + Constants.APOSTROPHE;
                }
            } else {
                // 其他的简单操作
                str += this.opt.symbol();
                if (this.value != null) {
                    str += this.value;
                }
            }
        }
        return str;
    }


    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Func getFunc() {
        return func;
    }

    public void setFunc(Func func) {
        this.func = func;
    }

    public Opt getOpt() {
        return opt;
    }

    public void setOpt(Opt opt) {
        this.opt = opt;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
