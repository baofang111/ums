package com.bf.core.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * Created by bf on 2017/9/9.
 */
public class StatelessToken implements AuthenticationToken {

    private String username;

    private String password;

    private String clientId;

    public StatelessToken(String username, String password, String clientId) {
        this.username = username;
        this.password = password;
        this.clientId = clientId;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
