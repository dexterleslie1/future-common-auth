-- 用于初始化 auth 数据库

-- 创建用户表
CREATE TABLE IF NOT EXISTS `auth_user`
(
    id                  BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    loginName           VARCHAR(32)  UNIQUE COMMENT '登录名',
    phone               VARCHAR(64)  UNIQUE COMMENT '手机号码',
    email               VARCHAR(128) UNIQUE COMMENT '用户注册邮箱',
    `password`          VARCHAR(128) NOT NULL COMMENT '登录密码',
    createTime          DATETIME     NOT NULL COMMENT '创建时间',
    resetPasswordTime   DATETIME DEFAULT NULL COMMENT '上次重置密码时间',
    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 创建token表
CREATE TABLE IF NOT EXISTS `auth_token`
(
    id      BIGINT(20) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    userId  BIGINT(20) NOT NULL COMMENT '用户id',
    `type`  VARCHAR(16) NOT NULL COMMENT 'token类型：refresh和access token',
    token   VARCHAR(64) NOT NULL COMMENT 'token的值',
    createTime DATETIME NOT NULL COMMENT '创建时间',
    CONSTRAINT uni_authToken_userId_and_type UNIQUE(userId,`type`)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
