package com.future.common.auth.service;

import com.future.common.auth.entity.AuthToken;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

// todo 如果refresh token有过期时间，为何微信不需要再次登录呢？
@Slf4j
public abstract class TokenService {
    /**
     * access token的过期秒数
     */
    public final static int TtlAccessTokenInSeconds = 2 * 3600;
    /**
     * refresh token的过期秒数
     */
    public final static int TtlRefreshTokenInSeconds = 30 * 24 * 3600;

    /**
     * 用户登录或者refresh token时候分配token
     *
     * @param userId 用户ID
     * @param type   token 类型，参考 {@link AuthTokenType}
     */
    abstract String assign(Long userId, AuthTokenType type) throws NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * 调用接口时拦截器校验 token 是否合法、是否过期、是否已经被注销等
     *
     * @param token
     * @param type
     * @return 用户ID
     * @throws BusinessException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public Long validate(String token, AuthTokenType type) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (log.isDebugEnabled())
            log.debug("请求校验token {} 类型 {}", token, type);

        AuthToken authToken = this.get(token, type);
        if (authToken == null) {
            if (log.isDebugEnabled()) {
                log.debug("token {} type {} 不存在", token, type);
            }
            throw new BusinessException(ErrorCodeConstant.ErrorCodeLoginRequired, "您未登录");
        }

        // 校验token类型是否匹配
        try {
            this.validateTokenType(authToken.getType(), type);
            if (log.isDebugEnabled()) {
                log.debug("token {} type {} 通过类型匹配校验", token, type);
            }
        } catch (IllegalArgumentException ex) {
            if (log.isDebugEnabled())
                log.debug("token {} 类型 {} 和 类型 {} 不匹配", token, authToken.getType(), type);

            throw ex;
        }

        // 校验token是否过期
        this.validateExpired(authToken);

        if (log.isDebugEnabled())
            log.debug("成功校验通过token {} 类型 {}", token, type);

        Long userId = authToken.getUserId();

        if (log.isDebugEnabled())
            log.debug("token {} 类型 {} 对应的用户id {}", token, type, userId);

        return userId;
    }

    /**
     * 根据 token+type 获取AuthToken
     *
     * @param token
     * @param type
     * @return
     * @throws BusinessException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    abstract AuthToken get(String token, AuthTokenType type) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * 校验实际的token类型是否和预期的token类型相符合
     *
     * @param tokenTypeActual
     * @param tokenTypeExpect
     */
    void validateTokenType(AuthTokenType tokenTypeActual, AuthTokenType tokenTypeExpect) {
        Assert.isTrue(tokenTypeActual.equals(tokenTypeExpect), "不存在token");
    }

    /**
     * 刷新 access token
     *
     * @param refreshToken
     * @return
     * @throws BusinessException
     */
    public String refreshAccessToken(String refreshToken) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        AuthToken authToken = this.get(refreshToken, AuthTokenType.Refresh);

        // 校验refresh token是否过期
        this.validateExpired(authToken);

        Long userId = authToken.getUserId();

        if (log.isDebugEnabled())
            log.debug("token {} 类型 {} 对应的用户id {}", refreshToken, AuthTokenType.Refresh, userId);

        // 校验access token是否过期，否则不能提前刷新
        // 注意：todo 印象中 OAuth2.0 支持提前刷新 Token
        /*QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("`type`", AuthTokenType.Access);
        authToken = this.authTokenMapper.selectOne(queryWrapper);
        boolean expired = false;
        try {
            this.validateExpired(authToken);
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCodeConstant.ErrorCodeTokenExpired)
                expired = true;
        }
        if (!expired)
            throw new BusinessException("不能提前刷新access token");*/

        return this.assign(userId, AuthTokenType.Access);
    }

    /**
     * 判断token是否已经过期
     *
     * @param authToken
     */
    private void validateExpired(AuthToken authToken) throws BusinessException {
        Assert.isTrue(authToken != null, "请指定token");

        Date createTime = authToken.getCreateTime();
        Date timeNow = new Date();
        long secondsDelta = (timeNow.getTime() - createTime.getTime()) / 1000L;
        int ttlInSeconds = authToken.getType() == AuthTokenType.Access ?
                this.getTtlAccessToken() : this.getTtlRefreshTokenInSeconds();
        if (secondsDelta > ttlInSeconds) {
            if (log.isDebugEnabled())
                log.debug("token {} 已经过期 {} 秒", authToken, secondsDelta - ttlInSeconds);

            throw new BusinessException(ErrorCodeConstant.ErrorCodeTokenExpired, "token已过期");
        }
    }

    /**
     * 获取access token的ttl秒数
     *
     * @return
     */
    public int getTtlAccessToken() {
        return TtlAccessTokenInSeconds;
    }

    /**
     * 获取refresh token的ttl秒数
     *
     * @return
     */
    public int getTtlRefreshTokenInSeconds() {
        return TtlRefreshTokenInSeconds;
    }
}
