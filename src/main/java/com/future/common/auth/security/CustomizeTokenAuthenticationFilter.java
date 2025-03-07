package com.future.common.auth.security;

import com.future.common.auth.config.FutureAuthProperties;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.entity.User;
import com.future.common.auth.service.TokenService;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.common.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 验证用户是否登录拦截器
@Component
public class CustomizeTokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    FutureAuthProperties futureAuthProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath().toLowerCase();
        // 不拦截的接口
        if (this.futureAuthProperties.getIgnoreUris() != null) {
            return this.futureAuthProperties.getIgnoreUris().contains(path)
                    || this.futureAuthProperties.getDefaultIgnoreUris().contains(path);
        } else
            return this.futureAuthProperties.getDefaultIgnoreUris().contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取请求中携带的token并在本地查询是否有此token，
            // 是，则构造Authentication对象并注入到请求上下文中
            String token = obtainBearerToken(request);
            if (StringUtils.hasText(token)) {
                User user = this.tokenService.validate(token, AuthTokenType.Access);

                if (user != null) {
                    CustomizeUser customizeUser = new CustomizeUser(user.getId());
                    CustomizeAuthentication authentication = new CustomizeAuthentication(customizeUser);
                    authentication.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // todo 研究为何一定要使用 catch(Exeption ex)处理，把try catch注释了应用运行不正常了
            if (ex instanceof BusinessException) {
                onErrorResponse(request, response, ((BusinessException) ex).getErrorCode(), ((BusinessException) ex).getErrorMessage());
            } else {
                logger.error(ex.getMessage(), ex);
                onErrorResponse(request, response, ErrorCodeConstant.ErrorCodeCommon, "网络繁忙，稍后重试！");
            }
        }
    }

    String obtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace("Bearer ", "");
    }

    void onErrorResponse(HttpServletRequest request,
                         HttpServletResponse response,
                         int errorCode,
                         String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectResponse<String> responseO = ResponseUtils.failObject(errorCode, message);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
    }
}
