package com.future.common.auth.controller;

import com.future.common.auth.service.UserRegisterService;
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
public class UserRegisterController {
    @Resource
    UserRegisterService userRegisterService;

    /**
     * 使用 手机/邮箱+验证码 注册
     *
     * @param phoneOrEmail
     * @param password
     * @param verificationCode
     * @return
     * @throws BusinessException
     */
    @PostMapping(value = "registerWithVerificationCode")
    public ObjectResponse<String> registerWithVerification(
            @RequestParam(name = "phoneOrEmail", defaultValue = "") String phoneOrEmail,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) throws BusinessException {
        this.userRegisterService.registerWithVerificationCode(phoneOrEmail, password, verificationCode);
        return ResponseUtils.successObject("注册成功");
    }

    /**
     * 使用帐号密码注册
     *
     * @param loginName
     * @param password
     * @return
     */
    @PostMapping("registerWithLoginName")
    public ObjectResponse<String> registerWithLoginName(
            @RequestParam(name = "loginName") String loginName,
            @RequestParam(name = "password") String password) throws BusinessException {
        this.userRegisterService.registerWithLoginName(loginName, password);
        return ResponseUtils.successObject("注册成功");
    }
}
