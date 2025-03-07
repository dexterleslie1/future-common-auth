package com.future.common.auth.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

// 设置mybatis-plus mapper包扫描路径
@MapperScan("com.future.common.auth.mapper")
// 设置spring组件包扫描路径
@ComponentScan("com.future.common.auth")
@EnableConfigurationProperties(FutureAuthProperties.class)
@PropertySource("classpath:default-config-for-auth-props.properties")
public class FutureAuthConfiguration {

}
