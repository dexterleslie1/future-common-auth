package com.future.common.auth.config;

import com.future.common.auth.controller.*;
import com.future.common.auth.security.CustomizeTokenAuthenticationFilter;
import com.future.common.auth.service.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

// 设置mybatis-plus mapper包扫描路径
@MapperScan("com.future.common.auth.mapper")
// 因为 auth 依赖分为 AuthorizationServer 和 ResourceServer 两个角色，所以不使用 @ComponentScan，需要按需要加载组件
@Import({
        ConfigFutureAuthWebSecurity.class, ConfigMyBatisPlus.class, CustomizeTokenAuthenticationFilter.class, ConfigSwagger.class,
        SmsService.class, JwtTokenService.class, UserLoginService.class, UserQueryService.class, UserRegisterService.class, VerificationCodeService.class,
        TokenController.class, UserLoginController.class, UserQueryController.class, UserRegisterController.class, VerificationCodeController.class
})
@EnableConfigurationProperties(FutureAuthProperties.class)
@PropertySource("classpath:default-config-for-auth-props.properties")
public class FutureAuthorizationServerConfiguration {

}
