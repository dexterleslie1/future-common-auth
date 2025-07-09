package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.common.auth.entity.User;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.exception.BusinessException;
import com.future.common.phone.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户注册业务
 */
@Slf4j
@Validated
public class UserRegisterService extends ServiceImpl<UserMapper, User> {
    @Resource
    VerificationCodeService verificationCodeService;

    /**
     * 使用 手机/邮箱+验证码 注册
     *
     * @param phoneOrEmail
     * @param password
     * @param verificationCode
     * @throws BusinessException
     */
    public void registerWithVerificationCode(@NotNull(message = "请提供手机号或者邮箱参数")
                                             @NotBlank(message = "请提供手机号或者邮箱参数") String phoneOrEmail,
                                             @NotNull(message = "没有提供登录密码参数")
                                             @NotBlank(message = "没有提供登录密码参数") String password,
                                             @NotNull(message = "请提供验证码参数")
                                             @NotBlank(message = "请提供验证码参数") String verificationCode) throws BusinessException {
        this.verificationCodeService.verifyPhoneOrEmail(phoneOrEmail);

        User user;
        boolean isPhone = PhoneUtil.isValid(phoneOrEmail);
        if (isPhone) {
            // 判断手机号码是否已经存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phoneOrEmail);
            user = this.getOne(queryWrapper, true);
            if (user != null) {
                if (log.isDebugEnabled()) {
                    log.debug("手机号码 {} 已经存在", phoneOrEmail);
                }

                throw new BusinessException("手机号码 " + phoneOrEmail + " 已经存在");
            }
        } else {
            // 判断手机号码是否已经存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", phoneOrEmail);
            user = this.getOne(queryWrapper, true);
            if (user != null) {
                if (log.isDebugEnabled()) {
                    log.debug("邮箱 {} 已经存在", phoneOrEmail);
                }

                throw new BusinessException("邮箱 " + phoneOrEmail + " 已经存在");
            }
        }

        // 校验验证码
        this.verificationCodeService.verify(phoneOrEmail, verificationCode);

        user = new User();
        if (isPhone) {
            user.setPhone(phoneOrEmail);
        } else {
            user.setEmail(phoneOrEmail);
        }
        user.setPassword(password);
        user.setCreateTime(LocalDateTime.now());
        this.save(user);
    }

    /**
     * 使用帐号密码注册
     *
     * @param loginName
     * @param password
     */
    public void registerWithLoginName(
            @NotNull(message = "请提供注册帐号")
            @NotBlank(message = "请提供注册帐号") String loginName, String password) throws BusinessException {
        // 判断帐号是否存在
        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.eq("loginName", loginName);
        User user = this.getOne(queryWrapper);
        if (user != null) {
            if (log.isDebugEnabled()) {
                log.debug("帐号 {} 已经存在", loginName);
            }
            throw new BusinessException("帐号 " + loginName + " 已经存在");
        }

        if (log.isDebugEnabled()) {
            log.debug("帐号 {} 不存在，可以继续注册", loginName);
        }

        // 插入数据
        user = new User();
        user.setLoginName(loginName);
        user.setPassword(password);
        user.setCreateTime(LocalDateTime.now());
        this.save(user);
    }
}
