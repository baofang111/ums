package com.bf;

import com.bf.config.properties.UmsDruidProperties;
import com.bf.config.properties.UmsRedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
// 写这个注解的话，不需要在 UmsDruidProperties 上面添加 @Component 或者 @Configuration 不然会创建两个实例
// @EnableConfigurationProperties({UmsDruidProperties.class, UmsRedisProperties.class})
public class UsermanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsermanagerApplication.class, args);
	}
}
