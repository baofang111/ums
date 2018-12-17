package com.bf.config;

import com.bf.core.filter.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  注册 JwtFilter
 *
 * Created by bf on 2017/9/9.
 */
@Configuration
public class WebConfig {
    /**
     * 配置过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(this.jwtAuthenticationTokenFilter());
        registration.addUrlPatterns("/*");
        registration.setName("sessionFilter");
        return registration;
    }


    @Bean(name = "jwtFilter")
    public JwtFilter jwtAuthenticationTokenFilter() {
        return new JwtFilter();
    }
}
