package com.future.common.auth;

import com.future.common.auth.config.DataSourceInitConfiguration;
import com.future.common.auth.config.FutureAuthConfiguration;
import com.future.common.auth.config.RedisMode;
import com.future.common.auth.config.RedisModeSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 auth 插件注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DataSourceInitConfiguration.class, FutureAuthConfiguration.class, RedisModeSelector.class})
public @interface EnableFutureAuthorization {
    RedisMode redisMode() default RedisMode.Standalone;
}
