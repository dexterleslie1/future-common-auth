package com.future.common.auth.security;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Data
public class CustomizeUser extends User {
    /**
     * 对应业务系统的用户id
     */
    private Long userId;

    public CustomizeUser(Long userId) {
        super(String.valueOf(userId), StringUtils.EMPTY, Collections.emptyList());
        this.userId = userId;
    }

}
