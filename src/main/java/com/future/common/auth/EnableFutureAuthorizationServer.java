package com.future.common.auth;

import com.future.common.auth.config.DataSourceInitConfiguration;
import com.future.common.auth.config.FutureAuthorizationServerConfiguration;
import com.future.common.auth.config.RedisMode;
import com.future.common.auth.config.RedisModeSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用认证服务（颁发 JWT Token）插件注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DataSourceInitConfiguration.class, FutureAuthorizationServerConfiguration.class, RedisModeSelector.class})
public @interface EnableFutureAuthorizationServer {
    RedisMode redisMode() default RedisMode.Standalone;
}
