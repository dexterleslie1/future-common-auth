//package com.future.common.auth.service;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.future.auth.mapper.UserMapper;
//import com.future.auth.model.UserModel;
//import com.future.common.exception.BusinessException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.validation.annotation.Validated;
//
//import javax.annotation.Resource;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@Slf4j
//@Validated
//public class EmailService {
//
//    @Resource
//    VerificationCodeService verificationCodeService;
//    @Resource
//    UserMapper userMapper;
//
//    @Resource
//    RedisTemplate<String, String> redisTemplate;
//
//    /**
//     * 此方法被独立是因为方便mock测试
//     *
//     * @return
//     */
//    public String getSecretIdentifier() {
//        return UUID.randomUUID().toString();
//    }
//
//    /**
//     * 发送重置密码链接到邮箱
//     *
//     * @param email
//     */
//    public void sendResetPasswordLink(@NotNull(message = "没有提供邮箱参数")
//                                      @NotBlank(message = "没有提供邮箱参数") String email) throws BusinessException {
//        // 判断邮箱是否存在
//        QueryWrapper<UserModel> queryWrapper = Wrappers.query();
//        queryWrapper.eq("email", email);
//        UserModel userModel = this.userMapper.selectOne(queryWrapper);
//        if (userModel == null) {
//            log.debug("邮箱 {} 未在系统注册，导致无法发生邮件到此邮箱", email);
//            throw new BusinessException("邮箱 " + email + " 不存在，无法发送重置密码邮件链接");
//        }
//
//        String secretIdentifier = getSecretIdentifier();
//
//        // todo 思考是否需要判断已经存在secretIdentifier不需要重新生成
//        this.redisTemplate.opsForValue().set(secretIdentifier, email, 1, TimeUnit.DAYS);
//
//        // todo 生成链接并发送邮件
//    }
//
//    /**
//     * 发送验证码到指定邮箱
//     *
//     * @param email
//     * @throws Exception
//     */
//    public void sendVerificationCode(@NotNull(message = "没有提供邮箱参数")
//                                     @NotBlank(message = "没有提供邮箱参数") String email) throws Exception {
//        // todo 调用邮件发送网关发送验证码
//    }
//
//    /**
//     * 验证邮箱验证码
//     *
//     * @param email
//     * @param code
//     */
//    public void checkVerificationCode(@NotNull(message = "没有提供邮箱参数")
//                                      @NotBlank(message = "没有提供邮箱参数") String email,
//                                      @NotNull(message = "没有提供验证码参数")
//                                      @NotBlank(message = "没有提供验证码参数") String code) throws BusinessException {
//        try {
//            this.verificationCodeService.check(email, code);
//            log.debug("验证邮箱 {} 验证码成功", email);
//        } catch (BusinessException ex) {
//            String codeTemp = this.verificationCodeService.get(email);
//            log.debug("验证邮箱 {} 验证码失败，提供 {} 为错误值，正确值为 {}", email, code, codeTemp);
//            throw ex;
//        }
//    }
//
//}
