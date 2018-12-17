package com.bf.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.bf.config.properties.UmsDruidProperties;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *  数据源配置
 * Created by bf on 2017/6/25.
 */
@Configuration
public class DataSourcesConfig {

    // 使用类进行配置，也可以 实现 EnvironmentAware 接口 ，然后重写 setEnvironment ，进行自动配置
    @Autowired
    private UmsDruidProperties jdbcProperties;

    /**
     * druid初始化 -- 可配置多数据源项目
     * @return
     * @throws SQLException
     */
    @Primary //默认数据源
    @Bean(name = "dataSource",destroyMethod = "close")
    public DruidDataSource Construction() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcProperties.getUrl());
        dataSource.setUsername(jdbcProperties.getUsername());
        dataSource.setPassword(jdbcProperties.getPassword());
        dataSource.setDriverClassName(jdbcProperties.getDriverClassName());
        //配置最大连接
        dataSource.setMaxActive(jdbcProperties.getMaxActive());
        //配置初始连接
        dataSource.setInitialSize(jdbcProperties.getInitialSize());
        //配置最小连接
        dataSource.setMinIdle(jdbcProperties.getMinIdle());
        //连接等待超时时间
        dataSource.setMaxWait(jdbcProperties.getMaxWait());
        //间隔多久进行检测,关闭空闲连接
        dataSource.setTimeBetweenEvictionRunsMillis(jdbcProperties.getTimeBetweenEvictionRunsMillis());
        //一个连接最小生存时间
        dataSource.setMinEvictableIdleTimeMillis(jdbcProperties.getMinEvictableIdleTimeMillis());
        //用来检测是否有效的sql
        dataSource.setValidationQuery(jdbcProperties.getValidationQuery());
        dataSource.setTestWhileIdle(jdbcProperties.getTestWhileIdle());
        dataSource.setTestOnBorrow(jdbcProperties.getTestOnBorrow());
        dataSource.setTestOnReturn(jdbcProperties.getTestOnReturn());
        //打开PSCache,并指定每个连接的PSCache大小
        dataSource.setPoolPreparedStatements(jdbcProperties.getPoolPreparedStatements());
        dataSource.setMaxOpenPreparedStatements(jdbcProperties.getMaxPoolPreparedStatementPerConnectionSize());
        //配置sql监控的filter
        dataSource.setFilters(jdbcProperties.getFilters());
        // 通过 connectionProperties 打开 mergerSql 的慢查询功能 slowSqlMillis 设置输出查询超过 5000毫秒的sql
        dataSource.setConnectionProperties(jdbcProperties.getConnectionProperties());
        // 合并多个 DruidDataSource 的监控
        dataSource.setUseGlobalDataSourceStat(jdbcProperties.getUseGlobalDataSourceStat());
        try {
            dataSource.init();
        } catch (SQLException e) {
            throw new RuntimeException("druid datasource init fail");
        }
        return dataSource;
    }

    /**
     *  spring boot  使用 ServletRegistrationBean 来 配置 Servlet (不需要在web.xml 中配置了)
     *
     *  FilterRegistrationBean , ServletListenerRegistrationBean  则分别 用来配置 Filter ， 和Listener
     * @return
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        servletRegistrationBean.setServlet(new StatViewServlet());
        // servlet 映射地址
        servletRegistrationBean.addUrlMappings("/druid/*");
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("loginUsername", "admin");// 用户名
        initParameters.put("loginPassword", "123456");// 密码
        initParameters.put("resetEnable", "false");// 禁用HTML页面上的“Reset All”功能
        initParameters.put("allow", "127.0.0.1"); // IP白名单 (没有配置或者为空，则允许所有访问)
        // initParameters.put("deny", "192.168.20.38");// IP黑名单
        // (存在共同时，deny优先于allow)
        // Servlet  的一些初始化配置
        servletRegistrationBean.setInitParameters(initParameters);
        return servletRegistrationBean;
    }

    /**
     *  配置 Druid 的 Filter
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        // 拦截路劲
        filterRegistrationBean.addUrlPatterns("/*");
        // 过滤的条件，过滤掉静态资源
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*");
        // 如果你的user信息保存在cookie中，你可以配置principalCookieName，使得druid知道当前的user是谁
        // filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        // 同理 你可以配置principalSessionName，使得druid能够知道当前的session的用户是谁
        // filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return filterRegistrationBean;
    }

    /**
     *  按照BeanId来拦截配置 用来bean的监控
     */
    @Bean(value = "druid-stat-interceptor")
    public DruidStatInterceptor DruidStatInterceptor() {
        DruidStatInterceptor druidStatInterceptor = new DruidStatInterceptor();
        return druidStatInterceptor;
    }

    /**
     *  自动创建 事务代理
     * @return
     */
    @Bean
    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
        beanNameAutoProxyCreator.setProxyTargetClass(true);
        // 设置要监控的bean的id
        //beanNameAutoProxyCreator.setBeanNames("sysRoleMapper","loginController");
        beanNameAutoProxyCreator.setInterceptorNames("druid-stat-interceptor");
        return beanNameAutoProxyCreator;
    }

}
