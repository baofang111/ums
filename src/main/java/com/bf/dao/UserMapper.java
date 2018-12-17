package com.bf.dao;

import com.bf.common.entity.User;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by bf on 2017/7/30.
 */
public interface UserMapper extends Mapper<User>{

    /**
     *  通过账号查询用户，过滤删除状态下的用户
     * @param account
     * @return
     */
    User getByAccount(@Param("account") String account);




}
