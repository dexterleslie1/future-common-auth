package com.future.common.auth.controller;

import com.future.common.auth.service.VerificationCodeService;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "验证码相关接口", description = "获取验证码接口")
@RestController
@RequestMapping(value = "/api/v1/future/auth/verificationCode")
@Slf4j
public class VerificationCodeController {
    @Resource
    VerificationCodeService verificationCodeService;

    @ApiOperation(value = "注册、登录、重置密码业务场景用于发送验证码到手机号或者邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phoneOrEmail", value = "手机、邮箱", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping(value = "get")
    public ObjectResponse<Integer> get(
            @RequestParam(value = "phoneOrEmail", defaultValue = "") String phoneOrEmail) {
        int ttlInSeconds = this.verificationCodeService.get(phoneOrEmail);
        return ResponseUtils.successObject(ttlInSeconds);
    }
}
