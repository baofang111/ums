package com.bf.dao;

import com.bf.common.entity.Menu;
import com.bf.common.node.MenuNode;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 *  菜单表 Mapper 接口
 * Created by bf on 2017/7/30.
 */
public interface MenuMapper extends Mapper<Menu> {

    /**
     *  通过角色 ID 获取 资源URL
     * @param roleId
     * @return
     */
    List<String> getResUrlsByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据角色id 获取菜单
     * @param roles
     * @return
     */
    List<MenuNode> getMenusByRoleIds(List<Integer> roles);
}
