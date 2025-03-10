package com.future.common.auth.controller;

import com.future.common.auth.service.UserRegisterService;
import com.future.common.exception.BusinessException;
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

@Api(value = "注册相关接口", description = "手机/邮箱+验证码注册、帐号+密码注册接口")
@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class UserRegisterController {
    @Resource
    UserRegisterService userRegisterService;

    @ApiOperation(value = "使用 手机/邮箱+验证码 注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneOrEmail", value = "手机、邮箱", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "登录密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "verificationCode", value = "手机、邮箱接收到的验证码", required = true, dataType = "String", paramType = "query"),
    })
    @PostMapping(value = "registerWithVerificationCode")
    public ObjectResponse<String> registerWithVerification(
            @RequestParam(name = "phoneOrEmail", defaultValue = "") String phoneOrEmail,
            @RequestParam(name = "password", defaultValue = "") String password,
            @RequestParam(name = "verificationCode", defaultValue = "") String verificationCode) throws BusinessException {
        this.userRegisterService.registerWithVerificationCode(phoneOrEmail, password, verificationCode);
        return ResponseUtils.successObject("注册成功");
    }

    @ApiOperation(value = "使用 帐号+密码 注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName", value = "帐号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "登录密码", required = true, dataType = "String", paramType = "query"),
    })
    @PostMapping("registerWithLoginName")
    public ObjectResponse<String> registerWithLoginName(
            @RequestParam(name = "loginName") String loginName,
            @RequestParam(name = "password") String password) throws BusinessException {
        this.userRegisterService.registerWithLoginName(loginName, password);
        return ResponseUtils.successObject("注册成功");
    }
}
