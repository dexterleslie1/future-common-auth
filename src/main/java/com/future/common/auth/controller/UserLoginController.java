package com.future.common.auth.controller;

import com.future.common.auth.dto.LoginSuccessDTO;
import com.future.common.auth.service.UserLoginService;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "登录相关接口", description = "用户登录接口")
@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class UserLoginController {
    @Resource
    UserLoginService userLoginService;

    @ApiOperation(value = "使用 手机/邮箱+验证码 登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneOrEmail", value = "手机、邮箱", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "verificationCode", value = "手机、邮箱接收到的验证码", required = true, dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "loginWithVerificationCode")
    public ObjectResponse<LoginSuccessDTO> loginWithVerificationCode(
            @RequestParam(name = "phoneOrEmail", defaultValue = "") String phoneOrEmail,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) throws Exception {
        return ResponseUtils.successObject(this.userLoginService.loginWithVerificationCode(phoneOrEmail, verificationCode));
    }

    @ApiOperation(value = "使用 帐号/手机/邮箱+密码 登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginNameOrPhoneOrEmail", value = "帐号、手机、邮箱", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true, dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "loginWithPassword")
    public ObjectResponse<LoginSuccessDTO> loginWithLoginName(
            @RequestParam(name = "loginNameOrPhoneOrEmail") String loginNameOrPhoneOrEmail,
            @RequestParam(name = "password") String password) throws Exception {
        return ResponseUtils.successObject(this.userLoginService.loginWithPassword(loginNameOrPhoneOrEmail, password));
    }
}
