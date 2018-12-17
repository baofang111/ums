package com.bf.core.shiro.factory;

import com.bf.core.util.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 *  使用 SpringContextHolder 必须 配置 @DependsOn
 * Created by bf on 2017/8/13.
 */
@Service
@DependsOn("springContextHolder")
public class RedisShiroFactory implements IRedisShiro {

    /** 登录次数限制 的 redisKey*/
    private static final String SHIRO_LOGIN_COUNT = "shiro_login_count";

    /** 登录次数大于规定次数 锁定账号的 redisKey*/
    private static final String SHIRO_ACOUNT_LOCK = "shiro_acount_lock";

    /** redis 链接符*/
    private static final String REDIS_KEY_LINK = "_";


    /** redis 模板操作类*/
    @Autowired
    StringRedisTemplate redisTemplate;

    /** 使用 opsForValue 获取一个默认操作实例*/
    public ValueOperations<String, String> getRedis(){
        return redisTemplate.opsForValue();
    }

    /**
     *  获取IShiro 对象
     * @return
     */
    public static IRedisShiro me(){
        return SpringContextHolder.getBean(IRedisShiro.class);
    }

    @Override
    public void addLoginCount(final String acount) {
        /** redis increment 登录次数计数，redis 单线程 运行，该方法是 并发安全的*/
        this.getRedis().increment(SHIRO_LOGIN_COUNT + REDIS_KEY_LINK + acount, 1L);
    }

    @Override
    public int getLoginCount(final String acount) {
        /** 获取用户当前回话的登录次数*/
        return Integer.parseInt(this.getRedis().get(SHIRO_LOGIN_COUNT + REDIS_KEY_LINK + acount));
    }

    @Override
    public void resetLoginCount(final String acount) {
        /** 登录成功需要将次数重新设置为 0 一定不要忘了 自己和数据库进行匹配*/
        this.getRedis().set(SHIRO_LOGIN_COUNT + REDIS_KEY_LINK + acount, "0");
    }

    @Override
    public void lockAcount(final String acount, final long timeout) {
        /** 大于规定次数 记录状态*/
        this.getRedis().set(SHIRO_ACOUNT_LOCK + REDIS_KEY_LINK + acount, "LOCK");
        /** 锁定一小时*/
        redisTemplate.expire(SHIRO_ACOUNT_LOCK + REDIS_KEY_LINK + acount, timeout, TimeUnit.HOURS);
    }

    @Override
    public boolean checkLock(final String acount) {
        return  "LOCK".equals(this.getRedis().get(SHIRO_ACOUNT_LOCK + REDIS_KEY_LINK + acount));
    }
}
