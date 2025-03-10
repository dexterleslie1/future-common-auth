package com.future.common.auth.controller;


import com.future.common.auth.dto.UserDTO;
import com.future.common.auth.security.CustomizeAuthentication;
import com.future.common.auth.service.UserQueryService;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "查询用户信息相关接口", description = "获取个人信息、判断帐号是否存在等接口")
@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class UserQueryController {
    @Resource
    UserQueryService userQueryService;

    @ApiOperation(value = "获取个人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION, value = "accessToken", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("getSelfInfo")
    public ObjectResponse<UserDTO> getSelfInfo(CustomizeAuthentication authentication) throws BusinessException {
        return ResponseUtils.successObject(this.userQueryService.get(authentication.getUser().getUserId()));
    }

    @ApiOperation(value = "在注册时，判断帐号是否存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginName", value = "用户帐号", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("checkIfLoginNameExists")
    public ObjectResponse<Boolean> checkIfLoginNameExists(@RequestParam("loginName") String loginName) {
        return ResponseUtils.successObject(this.userQueryService.checkIfLoginNameExists(loginName));
    }
}
