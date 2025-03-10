package com.future.common.auth.controller;


import com.future.common.auth.service.TokenService;
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
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Api(value = "Token相关接口", description = "用户刷新Access Token等接口")
@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class TokenController {
    @Resource
    TokenService tokenService;

    @ApiOperation(value = "刷新 Access Token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refreshToken", value = "登录时候分配的 Refresh Token", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("refreshAccessToken")
    public ObjectResponse<String> refreshAccessToken(@RequestParam(value = "refreshToken") String refreshToken) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        return ResponseUtils.successObject(this.tokenService.refreshAccessToken(refreshToken));
    }
}
