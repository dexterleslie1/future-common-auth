package com.future.common.auth.controller;

import com.future.common.auth.dto.LoginSuccessDTO;
import com.future.common.auth.service.UserLoginService;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class UserLoginController {
    @Resource
    UserLoginService userLoginService;

    /**
     * 使用 手机/邮箱+验证码 登录
     *
     * @param phoneOrEmail
     * @param verificationCode
     * @return
     * @throws BusinessException
     */
    @PostMapping(value = "loginWithVerificationCode")
    public ObjectResponse<LoginSuccessDTO> loginWithVerificationCode(
            @RequestParam(name = "phoneOrEmail", defaultValue = "") String phoneOrEmail,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) throws Exception {
        return ResponseUtils.successObject(this.userLoginService.loginWithVerificationCode(phoneOrEmail, verificationCode));
    }

    /**
     * 使用 帐号/手机/邮箱+密码 登录
     *
     * @param loginNameOrPhoneOrEmail
     * @param password
     * @return
     * @throws Exception
     */
    @PostMapping(value = "loginWithPassword")
    public ObjectResponse<LoginSuccessDTO> loginWithLoginName(
            @RequestParam(name = "loginNameOrPhoneOrEmail") String loginNameOrPhoneOrEmail,
            @RequestParam(name = "password") String password) throws Exception {
        return ResponseUtils.successObject(this.userLoginService.loginWithPassword(loginNameOrPhoneOrEmail, password));
    }
}
