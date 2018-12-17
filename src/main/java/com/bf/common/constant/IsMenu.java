package com.bf.common.constant;

/**
 * 是否是菜单的枚举
 *
 * Created by bf on 2017/8/31.
 */
public enum IsMenu {

    YES(1, "是"),
    NO(0, "不是");

    int code;
    String message;

    IsMenu(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
