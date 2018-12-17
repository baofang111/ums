package com.bf.core.shiro.factory;

import com.bf.common.constant.UserStatus;
import com.bf.common.entity.Dept;
import com.bf.common.entity.User;
import com.bf.core.shiro.ShiroUser;
import com.bf.core.util.Convert;
import com.bf.core.util.SpringContextHolder;
import com.bf.dao.DeptMapper;
import com.bf.dao.MenuMapper;
import com.bf.dao.UserMapper;
import com.bf.service.DeptService;
import com.bf.service.RoleService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * shiro 工厂实现类
 *  @DependsOn 初始化该Bean时，强制初始化某个类
 *  @Transactional 开启事务，且设置只读
 * Created by bf on 2017/8/2.
 */
@Service
@DependsOn("springContextHolder")
@Transactional(readOnly = true)
public class ShiroFactory implements IShiro {



    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;



    /**
     *  获取IShiro 对象
     * @return
     */
    public static IShiro me(){
        return SpringContextHolder.getBean(IShiro.class);
    }


    @Override
    public User user(final String account) {
        User user = userMapper.getByAccount(account);
        if (user  == null){
            // 账号不存在抛出异常
            throw new CredentialsException();
        }
        if(user.getStatus().intValue() != UserStatus.OK.getCode()){
            // 帐户为不可用状态
            throw new LockedAccountException();
        }
        return user;
    }

    @Override
    public ShiroUser shiroUser(final User user) {
        ShiroUser shiroUser = new ShiroUser();
        shiroUser.setAccount(user.getAccount());
        shiroUser.setDeptId(user.getDeptid());
        shiroUser.setId(user.getId());
        shiroUser.setName(user.getName()); // 用户名称
        shiroUser.setDeptName(deptService.getDeptName(user.getDeptid())); //　部门名称

        Integer[] roleArray = Convert.toIntArray(user.getRoleid());// 角色集合
        List<Integer> roleList = new ArrayList<Integer>();
        List<String> roleNameList = new ArrayList<String>();

        for(int roleId : roleArray){
            roleList.add(roleId);
            roleNameList.add(roleService.getRoleName(roleId));
        }
        shiroUser.setRoleList(roleList);
        shiroUser.setRoleNames(roleNameList);

        return shiroUser;
    }

    @Override
    public String getRoleNameByRoleId(Integer roleId) {
        return roleService.getRoleName(roleId);
    }

    @Override
    public List<String> getPermissionByRoleId(Integer roleId) {
        return menuMapper.getResUrlsByRoleId(roleId);
    }
}
