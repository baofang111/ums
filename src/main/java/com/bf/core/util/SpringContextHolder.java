package com.bf.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring Application 的持有者，可以使用静态方法获取 spring 容器中的 Bean
 * Created by bf on 2017/7/30.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    /** sping applicationContext 对象*/
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    /** 根据 Bean 名称 获取 Bean*/
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    /** 根据Bean 类型获取Bean*/
    public static <T> T getBean(Class<T> requiredType) {
        assertApplicationContext();
        return applicationContext.getBean(requiredType);
    }


    /** 检验对象是否为空*/
    private static void assertApplicationContext(){
        if (SpringContextHolder.applicationContext == null){
            throw new RuntimeException("applicationContext属性为空，请检查是否注入了SpringContextHolder");
        }
    }
}
