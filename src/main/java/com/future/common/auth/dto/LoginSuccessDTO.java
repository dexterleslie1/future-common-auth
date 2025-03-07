package com.future.common.auth.dto;

import lombok.Data;

@Data
public class LoginSuccessDTO {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 手机号码
     */
    private String phone;
    private String loginName;
    private String email;
    /**
     * 用于获取最新access token的刷新 token
     */
    private String refreshToken;
    /**
     * 用于调用接口的access token
     */
    private String accessToken;
}
