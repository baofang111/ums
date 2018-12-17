package com.bf.service;

/**
 * 角色service
 * Created by bf on 2017/8/9.
 */
public interface RoleService {

    /**
     * 根据角色id 获取角色名称
     * @param roleId
     * @return
     */
    String getRoleName(Integer roleId);
}
