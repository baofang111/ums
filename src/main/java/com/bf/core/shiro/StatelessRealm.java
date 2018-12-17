package com.bf.core.shiro;

import com.bf.common.entity.User;
import com.bf.core.shiro.factory.IRedisShiro;
import com.bf.core.shiro.factory.IShiro;
import com.bf.core.shiro.factory.RedisShiroFactory;
import com.bf.core.shiro.factory.ShiroFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *  无状态的 realm
 * Created by bf on 2017/9/9.
 */
public class StatelessRealm extends AuthorizingRealm {


    @Override
    public boolean supports(AuthenticationToken token) {
        //仅支持StatelessToken类型的Token
        return token instanceof StatelessToken;
    }

    /**
     * 权限认证 , rolesList permissionList
     *  具体对象方法用处可查看 开涛的shiro 博客 --- http://jinnianshilongnian.iteye.com/blog/2022468
     *
     *  权限校验的时候，配置了 anon 的不会走这个方法
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取封装的 shiroUser 对象
        IShiro shiro = ShiroFactory.me();
        ShiroUser shiroUser = (ShiroUser) principalCollection.getPrimaryPrincipal();
        // 获取权限集合
        List<Integer> roleList = shiroUser.getRoleList();

        // 权限集合 -- 就是该角色下可操作的 URL ，从数据库中读取
        Set<String> permissionSet = new HashSet<>();
        // 角色集合
        Set<String> roleNames = new HashSet<>();

        for(Integer roleId : roleList){
            // 添加用户权限
            List<String> permissions = shiro.getPermissionByRoleId(roleId);
            if(permissions != null && !permissions.isEmpty()){
                for(String permission : permissions){
                    permissionSet.add(permission);
                }
            }
            roleNames.add(shiro.getRoleNameByRoleId(roleId));
        }
        // 返回参数
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(roleNames);
        authorizationInfo.addStringPermissions(permissionSet);
        return authorizationInfo;
    }

    /**
     *  身份认证 AuthenticationInfo -- 用来进行身份认证的
     *
     *      密码正确只访问一次，密码错误的时候访问两次
     *
     *      猜测是 hashMap.put("/login", "authc,kickout"); 这个配置里面的 这个  authc 需要验证，所以走了一次这个方法，然后再subject。login 的时候又走了一下这个方法
     *      当 吧 login 中的 authc 去掉，就不走这个地方了，不然默认 执行炼就会多走一次这个地方
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        StatelessToken token = (StatelessToken) authenticationToken;
        String username = token.getUsername();

        IShiro shiro = ShiroFactory.me();
        IRedisShiro redisShiro = RedisShiroFactory.me();
        User user = shiro.user(username);
        // 扩展user对象，让他在授权的时候（PrincipalCollection.getPrimaryPrincipal 可以在得到这个对象）可以携带更多的参数
        ShiroUser shiroUser = shiro.shiroUser(user);
        // 获取用户密码
        String password = user.getPassword();
        String acount = shiroUser.getAccount();

        //TODO 功能有问题，每次访问login 方法两次方法，造成计数不准
        redisShiro.addLoginCount(acount);
        /**  校验登录次数是否大于规定次数*/
        if( redisShiro.getLoginCount(acount)>= 5){
            redisShiro.lockAcount(acount, 1);
        }
        /** 判断是否是锁定状态，锁定的话，抛出异常*/
        if(redisShiro.checkLock(acount)){
            throw new DisabledAccountException("亲，密码错误次数达到上限哦，为保护您的帐户安全，请一小时后重试哦！");
        }

        return new SimpleAuthenticationInfo(username, password,  super.getName());
    }


}
