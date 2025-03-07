package com.future.common.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String phone;
    private String email;
    private String loginName;
    private LocalDateTime createTime;
}
