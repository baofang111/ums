package com.bf.core.shiro;

import com.bf.common.entity.User;
import com.bf.core.shiro.factory.IRedisShiro;
import com.bf.core.shiro.factory.IShiro;
import com.bf.core.shiro.factory.RedisShiroFactory;
import com.bf.core.shiro.factory.ShiroFactory;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 自定义 realm
 *  最基础的realm接口
 *      CachingRealm 负责缓存处理
 *      AuthenticationRealm 负责认证处理
 *      AuthorizingRealm 负责授权处理
 *  通常的话，都是需要完成身份认证和权限认证操作的，所以一般继承AuthorizingRealm类
 *
 *  使用redis 的计数功能，实现 登录失败次数限制
 *      spring-boot-starter-data-redis 然后配置redis, 使用 StringRedisTemplate 操作redis
 *   每个用户，每次回话，只会访问一次 doGetAuthenticationInfo 身份认证的方法
 *
 * Created by bf on 2017/8/2.
 */
public class ShiroRealm extends AuthorizingRealm {

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
        IShiro shiro = ShiroFactory.me();
        IRedisShiro redisShiro = RedisShiroFactory.me();
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user = shiro.user(token.getUsername());
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
        // 获取用户密码盐
        String salt = user.getSalt();
        // 加盐
        ByteSource byteSource = new Md5Hash(salt);

        // 获取 SimpleAuthenticationInfo 对象，密码使用 salt 加盐处理
        return new SimpleAuthenticationInfo(shiroUser, password, byteSource, super.getName());
    }

    /**
     * 也可以直接在配置文件中
     *
     * 如果是密码加盐处理的话，必须加这个，告诉 shiro 已什么加密方式去加密，然后和密码进行匹配
     *  不然会抛出 did not match the expected credentials 异常
     * 设置认证加密方式
     */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
        md5CredentialsMatcher.setHashAlgorithmName(ShiroKit.HASHMD5SLAT);
        md5CredentialsMatcher.setHashIterations(ShiroKit.HASHINTERATIONS);
        super.setCredentialsMatcher(md5CredentialsMatcher);
    }
}
