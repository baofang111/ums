package com.bf.core.shiro.factory;

import com.bf.common.entity.User;
import com.bf.core.shiro.ShiroUser;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

import java.util.List;

/**
 * Shiro 工厂接口 -- 获取 自定义realm 的一些常用数据接口
 * Created by bf on 2017/8/2.
 */
public interface IShiro {

    /**
     * 根据帐户查询出用户
     * @param account
     * @return
     */
    User user(String account);


    /**
     * 根据 user 对象 扩展到 ShiroUser
     * @param user
     * @return
     */
    ShiroUser shiroUser(User user);


    /**
     *  根据角色 roleId 查询角色名称
     * @param roleId 角色id
     * @return
     */
    String getRoleNameByRoleId(Integer roleId);

    /**
     *  通过角色ID roleId 查询该角色Id下的所有权限
     * @param roleId
     * @return
     */
    List<String> getPermissionByRoleId(Integer roleId);



}
