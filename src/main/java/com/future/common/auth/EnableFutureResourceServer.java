package com.future.common.auth;

import com.future.common.auth.config.FutureResourceServerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用资源服务（校验 JWT Token）插件注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FutureResourceServerConfiguration.class})
public @interface EnableFutureResourceServer {

}
