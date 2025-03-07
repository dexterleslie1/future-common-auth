package com.future.common.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName(value = "auth_user", autoResultMap = true)
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String phone;
    private String loginName;
    private String email;
    // todo 防止从数据库加载此字段数据
    private String password;
    private LocalDateTime createTime;
}
