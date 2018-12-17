package com.bf.config;

import com.bf.core.shiro.KickoutSessionControlFilter;
import com.bf.core.shiro.ShiroRealm;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
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
 *    @Configuration 注明这个类是一个配置类，这样配置的@Bean 才能生效，将 ShiroConfig 配置 spring 的上下文中
 *    @EnableConfigurationProperties    通过这个注解，将 umsRedisProperties 配置在上下文中，这样使用@Autowired 才能注入进来
 *    @PropertySource 指定配置文件
 *    ------------> 上面的方式注入全部失败，原因不明
 *
 *    采用 EnvironmentAware 这种方式注入可以
 *      实现 EnvironmentAware  重写方法 setEnvironment 可以在工程启动时，获取系统环境变量和 application 上下文中的变量
 *      还有一种方法就是 指定配置文件后 使用 @Value 注解（这里无效，其他地方没问题）
 *
 * Created by bf on 2017/8/2.
 */
//@Configuration
// @PropertySource(value = "classpath:/application.yml")
// @EnableConfigurationProperties(UmsRedisProperties.class)
public class ShiroConfig implements EnvironmentAware {

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

    /**
     *  向安全管理器中注入
     *
     *   rememberMe - 记住我
     *   记住我功能 CookieRememberMeManager 需要 simpleCookie 对象
     *   sessionManager - session管理
     */
    //@Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(this.shiroRealm());
        securityManager.setRememberMeManager(this.rememberMeManager());
        securityManager.setCacheManager(this.cacheManager());
        securityManager.setSessionManager(this.sessionManager());
        return securityManager;
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

    /**
     * cacheManager 缓存 redis实现
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(this.redisManager());
        return redisCacheManager;
    }



    /**
     * 自定义 realm
     * @return
     */
    //@Bean
    public ShiroRealm  shiroRealm(){
        return new ShiroRealm();
    }

    /**
     * cookie对象;
     * rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
     * @return
     */
    //@Bean
    public SimpleCookie rememberMeCookie(){
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //记住我生效时间 7 天
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(7 * 24 * 60 * 60);
        return simpleCookie;
    }

    /**
     * cookie管理对象;
     * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中
     */
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(this.rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        cookieRememberMeManager.setCipherKey(Base64.decode("3AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    /**
     * 设置 redisSessionDao shiro sessionDao 的实现 通过redis
     *  使用 shiro-redis 插件
     */
    //@Bean
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(this.redisManager());
        return redisSessionDAO;
    }

    /**
     *  配置 session manager
     *
     *  这里的 session 设置超时时间等，可能会被其他设置给覆盖
     */
    //@Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(this.redisSessionDAO());
        sessionManager.setGlobalSessionTimeout(30 * 60 * 1000); // session 失效时间
        sessionManager.setGlobalSessionTimeout(15 * 60 * 1000); // session 验证失效时间
        sessionManager.setDeleteInvalidSessions(true); // 是否禁用 session ID cookie,默认是开启的，如果禁用后将不设置session id Cookie,及使用 servlet 容器的session id，且通过 URL 重写来保存session id
        Cookie cookie = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
        cookie.setName("shiroCookie"); // 设置session的名字，默认为JSESSIONID
        cookie.setHttpOnly(true); // 设置为 true 则客户端不会暴露客户端脚本代码，有助于较少某些跨站点网站的脚本攻击，此特性需要实现了Servlet 2.5 MR6及以上版本的规范的Servlet容器支持；
        sessionManager.setSessionIdCookie(cookie); // 设置session id Cookie
        return sessionManager;
    }


    /**
     * session管理器(单机环境)
     */
/*    @Bean
    public DefaultWebSessionManager defaultWebSessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setCacheManager(this.cacheManager());
        sessionManager.setSessionValidationInterval(30 * 60 * 1000); // session 失效时间
        sessionManager.setGlobalSessionTimeout(15 * 60 * 1000); // session 验证失效时间
        sessionManager.setDeleteInvalidSessions(true); // 是否禁用 session ID cookie,默认是开启的，如果禁用后将不设置session id Cookie,及使用 servlet 容器的session id，且通过 URL 重写来保存session id
        sessionManager.setSessionValidationSchedulerEnabled(true);
        Cookie cookie = new SimpleCookie(ShiroHttpSession.DEFAULT_SESSION_ID_NAME);
        cookie.setName("shiroCookie"); // 设置session的名字，默认为JSESSIONID
        cookie.setHttpOnly(true); // 设置为 true 则客户端不会暴露客户端脚本代码，有助于较少某些跨站点网站的脚本攻击，此特性需要实现了Servlet 2.5 MR6及以上版本的规范的Servlet容器支持；
        sessionManager.setSessionIdCookie(cookie); // 设置session id Cookie
        return sessionManager;
    }*/

    /**
     * Shiro的过滤器链
     */
    //@Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        // 必须设置 安全管理器 -- nested exception is org.springframework.beans.factory.BeanInitializationException: SecurityManager property must be set
        shiroFilter.setSecurityManager(securityManager);
        Map<String, Filter> filters = new LinkedHashMap<>();
        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //限制同一帐号同时在线的个数。
        filtersMap.put("kickout", this.kickoutSessionControlFilter());
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
        hashMap.put("/static/**", "anon,kickout");
        hashMap.put("/login", "anon,kickout"); // authc 认证成功之后直接走 setSuccessUrl 登录成功后的URL
        hashMap.put("/global/sessionError", "anon");
        hashMap.put("/kaptcha", "anon");
        hashMap.put("/**", "user,kickout");
        shiroFilter.setFilterChainDefinitionMap(hashMap);
        return shiroFilter;
    }

    /**
     * 在方法中 注入 securityManager,进行代理控制
     */
    //@Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(DefaultWebSecurityManager securityManager) {
        MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
        bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        bean.setArguments(new Object[]{securityManager});
        return bean;
    }

    /**
     * 保证实现了Shiro内部lifecycle函数的bean执行
     */
    //@Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 启用shrio授权注解拦截方式，AOP式方法级权限检查 -- 开启注解判断权限
     */
    //@Bean
    //@DependsOn(value = "lifecycleBeanPostProcessor") //依赖其他bean的初始化
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    /**
     *  启用注解 权限校验
     * @param securityManager
     * @return
     */
    //@Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *  所以我们需要修改下doGetAuthenticationInfo中的代码;
     * ）
     * 也可以在 shiroRealm 中 重写 setCredentialsMatcher 方法，设置 HashedCredentialsMatcher 的值
     * md5 加盐处理必须 重写 HashedCredentialsMatcher，不然会抛出 did not match the expected credentials 异常
     *
     *
     * 密码凭证在这里设置没有，必须设置一个Bean,然后传入到 自定义 realm 里面才能生效
     *
     * @return
     */
/*    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(ShiroKit.HASHMD5SLAT); //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(ShiroKit.HASHINTERATIONS); //散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }*/

    /**
     * 限制同一账号登录同时登录人数控制
     * @return
     */
    public KickoutSessionControlFilter kickoutSessionControlFilter(){
        KickoutSessionControlFilter kickoutSessionControlFilter = new KickoutSessionControlFilter();
        //使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
        //这里我们还是用之前shiro使用的redisManager()实现的cacheManager()缓存管理
        //也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
        kickoutSessionControlFilter.setCacheManager(this.cacheManager());
        //用于根据会话ID，获取会话进行踢出操作的；
        kickoutSessionControlFilter.setSessionManager(this.sessionManager());
        //是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
        kickoutSessionControlFilter.setKickoutAfter(false);
        //同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
        /** 这里的session 最大登录次数 和 session过期之后的登录是关系*/
        kickoutSessionControlFilter.setMaxSession(20); //被踢出后重定向到的地址；
        kickoutSessionControlFilter.setKickoutUrl("/login?kickout=1");
        return kickoutSessionControlFilter;
    }


}
