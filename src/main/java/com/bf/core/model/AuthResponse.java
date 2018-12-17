package com.bf.core.model;

/**
 * 返回 token  的对象 accessToken
 *
 * Created by bf on 2017/9/9.
 */
public class AuthResponse {

    // token 值
    private String token;
    // 客户端Id
    private String clientId;

    public AuthResponse(String token, String clientId) {
        this.token = token;
        this.clientId = clientId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
