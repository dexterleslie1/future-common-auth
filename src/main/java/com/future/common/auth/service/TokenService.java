package com.future.common.auth.service;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.common.auth.entity.AuthToken;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.entity.User;
import com.future.common.auth.mapper.AuthTokenMapper;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import com.future.common.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

// todo 如果refresh token有过期时间，为何微信不需要再次登录呢？
// todo jwt token逻辑未完成
@Service
@Slf4j
public class TokenService {
    /**
     * access token的过期秒数
     */
    public final static int TtlAccessTokenInSeconds = 2 * 3600;
    /**
     * refresh token的过期秒数
     */
    public final static int TtlRefreshTokenInSeconds = 30 * 24 * 3600;

    @Resource
    AuthTokenMapper authTokenMapper;
    @Resource
    UserMapper userMapper;

    @Value("${privateKey}")
    String privateKey;
    @Value("${publicKey}")
    String publicKey;

    /**
     * 是否使用jwt签发token，注意：系统当前只支持 JWT 即可（没有需求为非 JWT），所以硬编码为 true
     */
    private boolean tokenAssignedByJwt = true;

    /**
     * 用户登录或者refresh token时候分配token
     *
     * @param userId 用户ID
     * @param type   token 类型，参考 {@link AuthTokenType}
     */
    String assign(Long userId, AuthTokenType type) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 不使用jwt签发token
        if (!this.tokenAssignedByJwt) {
            String token = UUID.randomUUID().toString();

            if (log.isDebugEnabled())
                log.debug("随机生成的token {} 类型 {} 用户id {}", token, type, userId);

            // userId+type 是唯一的
            QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
            queryWrapper.eq("userId", userId);
            queryWrapper.eq("`type`", type);
            AuthToken authToken = this.authTokenMapper.selectOne(queryWrapper);
            if (authToken == null) {
                authToken = new AuthToken();
                authToken.setUserId(userId);
                authToken.setType(type);
                authToken.setToken(token);
                authToken.setCreateTime(new Date());
                this.authTokenMapper.insert(authToken);

                if (log.isDebugEnabled())
                    log.debug("插入token记录 token {}", authToken);
            } else {
                authToken.setToken(token);
                authToken.setCreateTime(new Date());
                this.authTokenMapper.updateById(authToken);

                if (log.isDebugEnabled())
                    log.debug("token已存在，更新token {}", authToken);
            }

            if (log.isDebugEnabled())
                log.debug("成功保存token {} 类型 {} 用户id {} 到数据库", token, type, userId);

            return token;
        } else {
            String token;
            try {
                token = JwtUtil.signWithPrivateKey(this.privateKey, new Consumer<JWTCreator.Builder>() {
                    @Override
                    public void accept(JWTCreator.Builder builder) {
                        LocalDateTime now = LocalDateTime.now();
                        Date expiresAt = Date.from(now.plusSeconds(
                                        type == AuthTokenType.Access ? TtlAccessTokenInSeconds : TtlRefreshTokenInSeconds)
                                .toInstant(ZoneOffset.ofHours(8)));
                        if (log.isDebugEnabled()) {
                            log.debug("token 过期时间为 {}", expiresAt);
                        }

                        builder.withClaim("tokenType", type.name())
                                .withClaim("userId", userId)
                                // 设置 JWT Token 过期时间
                                .withExpiresAt(expiresAt)
                                // 协助过期逻辑判断是否过期
                                .withIssuedAt(Date.from(now.toInstant(ZoneOffset.ofHours(8))));
                    }
                });
            } catch (Exception ex) {
                if (ex instanceof IllegalArgumentException) {
                    log.warn(ex.getMessage(), ex);
                } else {
                    log.error(ex.getMessage(), ex);
                }
                throw ex;
            }

            if (log.isDebugEnabled())
                log.debug("使用jwt生成的token {} 类型 {} 用户id {}", token, type, userId);

            return token;
        }
    }

    /**
     * 调用接口时拦截器校验 token 是否合法、是否过期、是否已经被注销等
     *
     * @param token
     * @param type
     * @return
     * @throws BusinessException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public User validate(String token, AuthTokenType type) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
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

        return this.userMapper.selectById(userId);
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
    AuthToken get(String token, AuthTokenType type) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (!this.tokenAssignedByJwt) {
            QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
            queryWrapper.eq("token", token);
            queryWrapper.eq("`type`", type);
            AuthToken authToken = this.authTokenMapper.selectOne(queryWrapper);
            if (authToken == null) {
                if (log.isDebugEnabled())
                    log.debug("token {} 类型 {} 不存在", token, type);

                throw new BusinessException("不存在token");
            }

            return authToken;
        } else {
            DecodedJWT decodedJWT = JwtUtil.verifyWithPublicKey(this.publicKey, token);
            AuthTokenType tokenTypeDecoded = decodedJWT.getClaim("tokenType").as(AuthTokenType.class);
            Long userId = decodedJWT.getClaim("userId").asLong();

            AuthToken authToken = new AuthToken();
            authToken.setToken(token);
            authToken.setType(tokenTypeDecoded);
            authToken.setUserId(userId);
            authToken.setCreateTime(decodedJWT.getIssuedAt());
            return authToken;
        }
    }

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
        QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
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
            throw new BusinessException("不能提前刷新access token");

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
