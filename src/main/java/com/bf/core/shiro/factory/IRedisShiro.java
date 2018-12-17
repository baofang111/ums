package com.bf.core.shiro.factory;


/**
 * shiro redis 工厂
 * Created by bf on 2017/8/13.
 */
public interface IRedisShiro {


    /**
     *  增加 account 用户的登录次数，每次加1
     * @param acount
     */
    void addLoginCount(String acount);

    /**
     *  获取该用户的登录次数
     * @param acount
     * @return
     */
    int getLoginCount(String acount);

    /**
     *  重置登录次数 为 0
     * @param acount
     */
    void resetLoginCount(String acount);

    /** 锁定当前用户  timeout 锁定时常 默认为 小时*/
    void lockAcount(String acount, long timeout);

    /**
     * 校验当前用户是否为锁定状态
     * */
    boolean checkLock(String acount);
}
