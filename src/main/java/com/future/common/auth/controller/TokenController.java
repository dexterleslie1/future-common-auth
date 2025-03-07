package com.future.common.auth.controller;


import com.future.common.auth.service.TokenService;
import com.future.common.exception.BusinessException;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class TokenController {
    @Autowired
    TokenService tokenService;

    /**
     * 刷新access token
     *
     * @return
     */
    @PostMapping("refreshAccessToken")
    public ObjectResponse<String> refreshAccessToken(@RequestParam(value = "refreshToken", defaultValue = "") String refreshToken) throws BusinessException, NoSuchAlgorithmException, InvalidKeySpecException {
        return ResponseUtils.successObject(this.tokenService.refreshAccessToken(refreshToken));
    }
}
