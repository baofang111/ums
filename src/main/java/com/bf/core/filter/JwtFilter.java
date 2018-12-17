package com.bf.core.filter;

import com.bf.common.constant.Constant;
import com.bf.common.entity.User;
import com.bf.config.properties.JwtProperties;
import com.bf.core.shiro.ShiroKit;
import com.bf.core.shiro.StatelessToken;
import com.bf.core.util.jwt.JwtTokenUtil;
import com.bf.dao.UserMapper;
import io.jsonwebtoken.Claims;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  1: 继承 OncePerRequestFilter 类。可以让拦截器值拦截一次
 *
 *  2: 继承 AccessControlFilter 实现自定义 shiro 的过滤器，不过需要在上面 shiroConfig 配置
 *
 * Created by bf on 2017/9/6.
 */
public class JwtFilter extends AccessControlFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse response) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            // 每次生成token 都会带这个，所以要判断
            authToken = requestHeader.substring(7);// 剔除 Bearer
            // 根据token 获取对应的 Claims
            Claims claims = jwtTokenUtil.getClaimsFromToken(authToken);

            if(!this.checkToken(claims, authToken)){
                // token 错误或者token 过期，返回相应的状态码 403

                return false;
            }
            String userName = (String) claims.get("name");
            try{
                // token 符合条件，委托给 shiro 进行登录
                StatelessToken token = new StatelessToken(userName, (String) claims.get("password"), claims.getId());
                ShiroKit.getSubject().login(token);
            }catch(Exception e){
                // 错误返回相应的状态码
                return false;
            }

        }else{
            // 没有携带 token 返回状态码
        }
        return true;
    }



    /**
     * 校验 token的正确性
     * @param claims
     * @param authToken
     * @return
     */
    private boolean checkToken(Claims claims, String authToken){
        // 检验 token 是否在 在线列表当中
        if(!this.isOnlineUser(claims)){
            return false;
        }
        // token 过期
        if(jwtTokenUtil.isTokenExpired(authToken)){
            return false;
        }

        //TODO 校验 token的验证次数 -- 同样使用redis

        return true;
    }

    private boolean isOnlineUser(Claims claims){
        if(null == claims){
            return false;
        }
        // 验证是否在在线登录列表当中 -- 登录的时候记录当前的 登录用户到 redis 当中 -- 在用户登录的时候保存到redis 中


/*        String userName = (String) claims.get("name");
        Map<String, List<String>> userSet = (Map<String, List<String>>) redisTemplate.opsForValue().get(Constant.ONLING_USER_LIST + Constant.SPILIT_SIGN + userName);
        if(userSet == null){
            return false;
        }*/

        return true;
    }

    public JwtProperties getJwtProperties() {
        return jwtProperties;
    }

    public void setJwtProperties(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }
}
