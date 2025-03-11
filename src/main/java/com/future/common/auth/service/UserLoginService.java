package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.future.common.auth.dto.LoginSuccessDTO;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.entity.User;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.exception.BusinessException;
import com.future.common.phone.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
public class UserLoginService extends ServiceImpl<UserMapper, User> {
    @Resource
    TokenService tokenService;
    @Resource
    VerificationCodeService verificationCodeService;

    EmailValidator emailValidator = EmailValidator.getInstance();

    /**
     * 使用 手机/邮箱+验证码 登录
     *
     * @param phoneOrEmail
     * @param verificationCode
     * @return
     * @throws BusinessException
     */
    public LoginSuccessDTO loginWithVerificationCode(String phoneOrEmail, String verificationCode) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        this.verificationCodeService.verifyPhoneOrEmail(phoneOrEmail);

        // 判断验证码是否正确
        this.verificationCodeService.verify(phoneOrEmail, verificationCode);

        boolean isPhone = PhoneUtil.isValid(phoneOrEmail);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(isPhone ? "phone" : "email", phoneOrEmail);
        User user = this.getOne(queryWrapper, true);

        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("尝试使用 手机/邮箱+验证码 {} 登录但失败", phoneOrEmail);
            }

            throw new BusinessException("提供的" + (isPhone ? "手机号码" : "邮箱") + "错误！");
        }

        // 校验密码通过后分配token
        Long userId = user.getId();

        String refreshToken = this.tokenService.assign(userId, AuthTokenType.Refresh);
        String accessToken = this.tokenService.assign(userId, AuthTokenType.Access);
        return createLoginSuccessDTO(user, refreshToken, accessToken);
    }

    /**
     * 使用 帐号/手机/邮箱+密码 登录
     *
     * @param loginNameOrPhoneOrEmail
     * @param password
     * @return
     * @throws BusinessException
     */
    public LoginSuccessDTO loginWithPassword(String loginNameOrPhoneOrEmail, String password) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        boolean isEmail = false;
        boolean isPhone = false;
        boolean isLoginName = false;

        isEmail = this.emailValidator.isValid(loginNameOrPhoneOrEmail);
        if (!isEmail) {
            // 不是邮箱时验证是否为手机号
            isPhone = PhoneUtil.isValid(loginNameOrPhoneOrEmail);
            if (!isPhone) {
                isLoginName = true;
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("登录唯一标识 {} isEmail {} isPhone {} isLoginName {}", loginNameOrPhoneOrEmail
                    , isEmail, isPhone, isLoginName);
        }

        QueryWrapper<User> queryWrapper = Wrappers.query();
        if (isEmail) {
            queryWrapper.eq("email", loginNameOrPhoneOrEmail).eq("password", password);
        } else if (isPhone) {
            queryWrapper.eq("phone", loginNameOrPhoneOrEmail).eq("password", password);
        } else {
            queryWrapper.eq("loginName", loginNameOrPhoneOrEmail).eq("password", password);
        }
        User user = this.getOne(queryWrapper, true);

        if (user == null) {
            if (log.isDebugEnabled()) {
                log.debug("尝试使用唯一标识 {} 密码 {} 登录但失败", loginNameOrPhoneOrEmail, password);
            }

            throw new BusinessException("提供的“帐号/手机/邮箱”或者密码错误！");
        }

        // 校验密码通过后分配token
        Long userId = user.getId();

        String refreshToken = this.tokenService.assign(userId, AuthTokenType.Refresh);
        String accessToken = this.tokenService.assign(userId, AuthTokenType.Access);
        return createLoginSuccessDTO(user, refreshToken, accessToken);
    }

    private LoginSuccessDTO createLoginSuccessDTO(User user
            , String refreshToken
            , String accessToken) {
        LoginSuccessDTO loginSuccessDTO = new LoginSuccessDTO();
        BeanUtils.copyProperties(user, loginSuccessDTO);
        loginSuccessDTO.setUserId(user.getId());
        loginSuccessDTO.setRefreshToken(refreshToken);
        loginSuccessDTO.setAccessToken(accessToken);
        return loginSuccessDTO;
    }
}
