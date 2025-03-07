package com.future.common.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "auth_token", autoResultMap = true)
public class AuthToken {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private AuthTokenType type;
    private String token;
    private Date createTime;

    /**
     * token是否过期
     */
    @TableField(exist = false)
    private boolean expired;
}
