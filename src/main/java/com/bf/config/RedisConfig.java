package com.bf.config;

import com.bf.config.properties.UmsRedisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedisPool redis 连接池配置类，可以获取redis，并通过redis操作缓存实例
 *      必须继承  缓存配置支持 CachingConfigurerSupport
 *
 *   配置redis 的时候，链接虚拟机中的redis 中，常见有两个错误
 *      一：connect time out 链接超时
 *          虚拟机中的redis 没有开放对外端口，且redis 本事配置中 bind 了端口号
 *          1: redis.config 中 注释掉 bind 127.0.0.1
 *          2: 关闭配置中的远程保护 protected-mode no
 *          3：开放 liunx 系统对外的端口号 - 6379
 *              centos 7 版本命令：（其他版本的centos 命令为iptables 具体自己百度）
 *                  开放端口：   firewall-cmd --zone=public --add-port=6379/tcp --permanent
 *                  重启防火墙：  firewall-cmd --reload
 *     二：Setup a bind address or an authentication password. NOTE: You only need to do one of the above things in order for the server to start accepting connections from the outside.
 *           设置 redis 默认密码
 *           1：打开 redis-cli 客户端 ./redis-cli
 *           2: 设置默认密码 credis.config 里面放开 requirepass 你的密码
 *
 *    三： NOAUTH Authentication required
 *          认证问题：
 *              1：redis 设置密码没有重启等原因
 *              2：或者设置密码之后，没有携带 redis.config 配置文件启动
 *
 * Created by bf on 2017/8/12.
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private UmsRedisProperties umsRedisProperties;

    /**
     * jedisPool 配置实例
     */
    @Bean
    public JedisPool redisPoolFactory(){
        JedisPoolConfig jedisConfig = new JedisPoolConfig();
        jedisConfig.setMaxIdle(umsRedisProperties.getMaxIdle());
        jedisConfig.setMaxWaitMillis(umsRedisProperties.getMaxWaitMillis());
        JedisPool jedisPool = new JedisPool(jedisConfig, umsRedisProperties.getHost(), umsRedisProperties.getPort(), umsRedisProperties.getTimeout(), umsRedisProperties.getPassword());
        return jedisPool;
    }

}
