package com.future.common.auth.config;

import com.future.common.auth.security.CustomizeTokenAuthenticationFilter;
import com.future.common.auth.service.JwtTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

// 因为 auth 依赖分为 AuthorizationServer 和 ResourceServer 两个角色，所以不使用 @ComponentScan，需要按需要加载组件
@Import({
        ConfigFutureAuthWebSecurity.class, CustomizeTokenAuthenticationFilter.class,
        JwtTokenService.class
})
@EnableConfigurationProperties(FutureAuthProperties.class)
@PropertySource("classpath:default-config-for-auth-props.properties")
public class FutureResourceServerConfiguration {

}
