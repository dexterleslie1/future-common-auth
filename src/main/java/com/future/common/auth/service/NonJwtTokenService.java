package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.common.auth.entity.AuthToken;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.mapper.AuthTokenMapper;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * 非 JWT Token 服务
 */
@Slf4j
public class NonJwtTokenService extends TokenService {
    @Resource
    AuthTokenMapper authTokenMapper;
    @Resource
    UserMapper userMapper;

    @Override
    String assign(Long userId, AuthTokenType type) {
        // 不使用jwt签发token
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
    }

    @Override
    AuthToken get(String token, AuthTokenType type) throws BusinessException {
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
    }
}
