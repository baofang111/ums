package com.bf.service.impl;

import com.bf.common.entity.Role;
import com.bf.core.util.ToolUtil;
import com.bf.dao.RoleMapper;
import com.bf.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by bf on 2017/8/9.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public String getRoleName(Integer roleId) {
        if( 0 == roleId){
            return "";
        }
        Role role = roleMapper.selectByPrimaryKey(roleId);
        if(role != null && StringUtils.isNotBlank(role.getName())){
            return role.getName();
        }
        return "";
    }
}
