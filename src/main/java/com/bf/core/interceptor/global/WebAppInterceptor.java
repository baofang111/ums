package com.bf.core.interceptor.global;

import com.bf.core.interceptor.SessionTimeOutInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 添加 interceptor 拦截器炼
 *  重写 WebMvcConfigurerAdapter 中的 addInterceptors 方法
 * Created by bf on 2017/8/14.
 */
@Configuration
public class WebAppInterceptor extends WebMvcConfigurerAdapter {

    /**
     * 添加拦截器炼
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //TODO 不用session管理了，相关功能后期改进
        // registry.addInterceptor(new SessionTimeOutInterceptor()).addPathPatterns("/**"); // session 超时拦截器
        super.addInterceptors(registry);
    }
}
