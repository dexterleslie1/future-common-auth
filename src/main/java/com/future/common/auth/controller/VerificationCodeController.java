package com.future.common.auth.controller;

import com.future.common.auth.service.VerificationCodeService;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// todo swagger
@RestController
@RequestMapping(value = "/api/v1/future/auth/verificationCode")
@Slf4j
public class VerificationCodeController {
    @Resource
    VerificationCodeService verificationCodeService;

    /**
     * 注册、登录、重置密码业务场景用于发送验证码到手机号或者邮箱
     *
     * @return
     */
    @GetMapping(value = "get")
    public ObjectResponse<Integer> get(
            @RequestParam(value = "phoneOrEmail", defaultValue = "") String phoneOrEmail) {
        int ttlInSeconds = this.verificationCodeService.get(phoneOrEmail);
        return ResponseUtils.successObject(ttlInSeconds);
    }
}
