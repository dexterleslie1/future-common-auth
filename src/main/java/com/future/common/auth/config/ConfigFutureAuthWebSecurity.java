package com.future.common.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.future.common.auth.security.CustomizeTokenAuthenticationFilter;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
// 保证此security配置在其他security配置（调用者的配置）前调用，
// 因为如果这个配置在其他配置后执行会覆盖之前的security配置
@Order(99999999)
public class ConfigFutureAuthWebSecurity extends WebSecurityConfigurerAdapter {

    @Resource
    CustomizeTokenAuthenticationFilter customizeTokenAuthenticationFilter;
    @Resource
    FutureAuthProperties futureAuthProperties;
    @Resource
    ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 合并插件配置的默认ignoreUris和调用者配置的ignoreUris
        List<String> ignoreUris = new ArrayList<>(this.futureAuthProperties.getDefaultIgnoreUris());
        if (this.futureAuthProperties.getIgnoreUris() != null && !this.futureAuthProperties.getIgnoreUris().isEmpty()) {
            ignoreUris.addAll(this.futureAuthProperties.getIgnoreUris());
        }

        http
                // 禁用 CSRF 保护
                .csrf().disable()
                // 用于指示Spring Security不应为客户端创建HTTP会话（即，不应在服务器上存储会话数据）。当您将此策略设置为无状态时，Spring Security将不会使用HTTP会话来跟踪用户身份或状态。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 允许匿名访问的接口，如：获取验证码、注册、登录等
                .and().authorizeRequests()
                .antMatchers(ignoreUris.toArray(new String[]{})).permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(customizeTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 异常处理设置
                .exceptionHandling()
                // 权限不足时处理
                .accessDeniedHandler(accessDeniedHandler())
                // 未登录时处理，即SecurityContextHolder中不存在Authentication对象时
                .authenticationEntryPoint(authenticationEntryPoint())
                // 禁用logout设置
                .and().logout().disable()
                // 禁用formlogin设置
                .formLogin().disable();
    }

    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ObjectResponse<String> responseO = ResponseUtils.failObject(50002, "权限不足");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(objectMapper.writeValueAsString(responseO));
            }
        };
    }

    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ObjectResponse<String> responseO = ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeLoginRequired, "您未登陆");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(objectMapper.writeValueAsString(responseO));
            }
        };
    }

}