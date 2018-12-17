package com.bf.config;

import com.bf.config.properties.JwtProperties;
import com.bf.core.filter.JwtFilter;
import com.bf.core.shiro.KickoutSessionControlFilter;
import com.bf.core.shiro.ShiroRealm;
import com.bf.core.shiro.StatelessDefaultSubjectFactory;
import com.bf.core.shiro.StatelessRealm;
import com.bf.core.util.jwt.JwtTokenUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 权限框架配置类
 *
 *  整合jwt 后，关闭session，
 *    1： 访问报错 This is an invalid application configuration
 *      在 shiroConfig 上面加入这个代码，
 *      是因为在 shiro 底层中 SecurityUtils 中导致的，具体看源码 getSecurityManager 方法，当 securityManager 为空的时候，抛出异常
 *              if(ThreadContext.getSecurityManager() == null){
                        ThreadContext.bind(securityManager);
                 }
 *    2：DisabledSessionException 异常 See the org.apache.shiro.subject.support.DisabledSessionException JavaDoc for more
 *      可看源码 DelegatingSubject implements Subject 中的 getSession 方法
 *          当 createSession 为 true 且 sessionCreationEnabled 为 false 的时候 ，就会包这个错误
 *               isSessionCreationEnabled属性，其默认值为true
 *
 *
 * Created by bf on 2017/8/2.
 */
@Configuration
// @PropertySource(value = "classpath:/application.yml")
// @EnableConfigurationProperties(UmsRedisProperties.class)
@EnableConfigurationProperties(JwtProperties.class)
public class StatelessShiroConfig implements EnvironmentAware {


/*    @Autowired
    private UmsRedisProperties umsRedisProperties;

    @Value("${spring.redis.host}")
    private String host;*/

    private RelaxedPropertyResolver propertyResolver;

    /**
     * 一定不能忘了  后面需要一个点  spring.redis.
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.redis.");
    }

    @Bean("statelessRealm")
    public StatelessRealm getStatelessRealm() {
        StatelessRealm statelessRealm = new StatelessRealm();
        statelessRealm.setCachingEnabled(false);
        return statelessRealm;
    }

    @Bean("subjectFactory")
    public StatelessDefaultSubjectFactory getStatelessDefaultSubjectFactory() {
        return new StatelessDefaultSubjectFactory();
    }

    /**
     *  shiro  缓存管理，注意在更改权限等操作的时候，清除缓存
     *  使用shiro-redis开源插件
     *  配置shiro redisManager
     */
    public RedisManager redisManager() {
        System.out.println("shiro host: " + propertyResolver.getProperty("host"));
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(propertyResolver.getProperty("host"));
        redisManager.setPort(Integer.parseInt(propertyResolver.getProperty("port")));
        // redisManager.setExpire(1800);// 配置过期时间
        redisManager.setTimeout(Integer.parseInt(propertyResolver.getProperty("timeout")));
        redisManager.setPassword(propertyResolver.getProperty("password"));
        return redisManager;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    /**
     * 启用shrio授权注解拦截方式，AOP式方法级权限检查 -- 开启注解判断权限
     */
    @Bean
    @DependsOn(value = "lifecycleBeanPostProcessor") //依赖其他bean的初始化
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }
    /**
     *  向安全管理器中注入
     *
     *   rememberMe - 记住我
     *   记住我功能 CookieRememberMeManager 需要 simpleCookie 对象
     *   sessionManager - session管理
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSubjectFactory(this.getStatelessDefaultSubjectFactory());
        securityManager.setRealm(this.getStatelessRealm());
        securityManager.setCacheManager(this.cacheManager());
        securityManager.setSessionManager(this.sessionManager());
        securityManager.setSubjectDAO(this.getSubjectDAO());
        return securityManager;
    }



    /**
     * cacheManager 缓存 redis实现
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(this.redisManager());
        return redisCacheManager;
    }




    /**
     *  配置 session manager
     *
     *  这里的 session 设置超时时间等，可能会被其他设置给覆盖
     */
    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 不启用session
        sessionManager.setSessionValidationSchedulerEnabled(false);
        return sessionManager;
    }


    @Bean
    public SubjectDAO getSubjectDAO() {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        return subjectDAO;
    }



    /**
     * Shiro的过滤器链
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();

        if(ThreadContext.getSecurityManager() == null){
            ThreadContext.bind(securityManager);
        }
        if(SecurityUtils.getSecurityManager() == null){
            SecurityUtils.setSecurityManager(securityManager);
        }


        // 必须设置 安全管理器 -- nested exception is org.springframework.beans.factory.BeanInitializationException: SecurityManager property must be set
        shiroFilter.setSecurityManager(securityManager);


        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。
        // filtersMap.put("kickout", this.kickoutSessionControlFilter());
        // 添加jwt
        filtersMap.put("jwtFilter", this.getJwtFilter());
        shiroFilter.setFilters(filtersMap);
        /**
         * 默认的登陆访问url
         */
        shiroFilter.setLoginUrl("/login");
        /**
         * 登陆成功后跳转的url
         */
        shiroFilter.setSuccessUrl("/");
        /**
         * 没有权限跳转的url
         */
        shiroFilter.setUnauthorizedUrl("/global/error");
        /**
         * 配置shiro拦截器链
         *
         * anon  不需要认证
         * authc 需要认证 -- 注意 我们在做一些登录判断的时候，（最大登录次数等等， /login 登录链接下 一定要是 authc 需认证状态）
         * user  验证通过或RememberMe登录的都可以
         *  也可以放入数据库中，进行动态的配置
         *
         *   我们想要让我们的 自定义拦截器生效的话，我们需要在想要生效的位置，加上拦截器名
         *      如：同时在线人数限制的拦截器 kickout ，我们就需要在 login 和 静态资源 static/** 上面全部加上这玩意
         *      只有加上 拦截器名 才走，不然不会走该拦截器
         *   */
        Map<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("/static/**", "anon");// kickout
        hashMap.put("/login", "anon"); // authc 认证成功之后直接走 setSuccessUrl 登录成功后的URL kickout
        hashMap.put("/global/sessionError", "anon");
        hashMap.put("/kaptcha", "anon");
        hashMap.put("/**", "user,jwtFilter"); // kickout
        shiroFilter.setFilterChainDefinitionMap(hashMap);
        return shiroFilter;
    }


    public JwtFilter getJwtFilter(){
        JwtFilter filter = new JwtFilter();
        return filter;
    }


}
