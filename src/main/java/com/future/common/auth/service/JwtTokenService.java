package com.future.common.auth.service;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.future.common.auth.entity.AuthToken;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Consumer;

/**
 * JWT Token 服务，注意：当前系统只支持 JWT Token。
 */
@Slf4j
public class JwtTokenService extends TokenService {
    @Value("${privateKey:}")
    String privateKey;
    @Value("${publicKey:}")
    String publicKey;

    @Override
    String assign(Long userId, AuthTokenType type) throws NoSuchAlgorithmException, InvalidKeySpecException {
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

    @Override
    AuthToken get(String token, AuthTokenType type) throws NoSuchAlgorithmException, InvalidKeySpecException {
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
