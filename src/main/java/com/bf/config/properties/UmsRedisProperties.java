package com.bf.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * redis 参数配置了；类
 *  注意redis 配置类，不要命令 RedisProperties ，和系统文件中的一个类重复
 *
 *     如果不加 @Component这个注解的话, 使用@Autowired 就不能注入进去了
 *
 *
 * Created by bf on 2017/8/12.
 */
@Component
@ConfigurationProperties(prefix = UmsRedisProperties.REDIS_PERFIX)
public class UmsRedisProperties {

    public static final String REDIS_PERFIX = "spring.redis";

    /** redis 主机 IP*/
    private String host = "127.0.0.1";

    /** redis 端口号*/
    private int port = 6379;

    /** 链接超时时间*/
    private int timeout = 0;

    /** redis 服务密码*/
    private String password = "123456";

    /** 最大空闲时间*/
    private int maxIdle = 0;

    /** 最大等待时间*/
    private long maxWaitMillis = -1;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
