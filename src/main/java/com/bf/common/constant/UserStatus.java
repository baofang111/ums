package com.bf.common.constant;

/**
 * 用户帐户状态的枚举
 * Created by bf on 2017/8/2.
 */
public enum UserStatus {

    OK(1, "启用"), FREEZED(2, "冻结"), DELETE(3, "删除");

    int code;
    String message;

    UserStatus(int code, String message) {
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
