package com.bf.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * 配置 spring-mybatis 配置
 * Created by bf on 2017/6/25.
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = "com.bf.dao.**")
public class MybatisConfig implements TransactionManagementConfigurer{

    // 使用 自己配置的数据源
    @Resource(name = "dataSource")
    private DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        // setting 配置
        org.apache.ibatis.session.Configuration mybatisSettinConfig = new org.apache.ibatis.session.Configuration();
        // 开启一级缓存
        mybatisSettinConfig.setCacheEnabled(true);
        // 开启驼峰匹配 prize_code 匹配实体类中的 prizeCode
        mybatisSettinConfig.setMapUnderscoreToCamelCase(true);

        bean.setConfiguration(mybatisSettinConfig);

        // 扫面别名
        bean.setTypeAliasesPackage("com.bf.common.entity");

        // 配置mybaits  的 一些 setting 操作
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setCacheEnabled(true);
        bean.setConfiguration(configuration);


        // 在这里可以田间插件 例如 分页助手 和 通用MAPPER

        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 扫描XML 文件
            bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     *  spring 事务管理器·
     * @return
     */
    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return  new DataSourceTransactionManager(dataSource);
    }
}
