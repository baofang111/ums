package com.bf.core.model;

/**
 * auth token 接受所需要的参数，可根据业务自行定义，以便实现更多功能
 * Created by bf on 2017/9/9.
 */
public class AuthRequest {

    // 客户端ID
    private String	clientId;
    // 用户名
    private String userName;
    // 密码
    private String password;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
