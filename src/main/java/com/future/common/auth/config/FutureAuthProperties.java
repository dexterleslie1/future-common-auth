package com.future.common.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

// 表示此自定义属性启用验证
// https://reflectoring.io/validate-spring-boot-configuration-parameters-at-startup/
@Validated
// 默认读取application.properties文件中自定义属性
// 自定义属性以com.future.common.auth开头
@ConfigurationProperties(prefix = "com.future.common.auth")
@Data
public class FutureAuthProperties {
    /**
     * 插件内部默认配置的spring security忽略的uri
     * 注意：调用者不需要配置此uri
     */
    private List<String> defaultIgnoreUris;

    /**
     * spring security忽略的uri
     */
    private List<String> ignoreUris;
}


