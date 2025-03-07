//package com.future.common.auth.service;
//
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.future.auth.mapper.UserMapper;
//import com.future.auth.model.UserModel;
//import com.future.common.exception.BusinessException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//import org.springframework.validation.annotation.Validated;
//
//import javax.annotation.Resource;
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import java.util.Date;
//
///**
// * 重置密码service层
// */
//@Service
//@Slf4j
//@Validated
//public class ResetPasswordService {
//
//    @Resource
//    UserMapper userMapper;
//    @Resource
//    RedisTemplate<String, String> redisTemplate;
//
//    /**
//     * 用户登录后重置密码
//     *
//     * @param userId
//     * @param currentPassword 当前密码，如果第一次设置密码，这个参数可以留空
//     * @param newPassword     新的密码
//     */
//    public void reset(@NotNull(message = "没有提供用户id参数")
//                      @Min(value = 1, message = "没有提供用户id参数") Long userId,
//                      String currentPassword,
//                      @NotNull(message = "没有提供新密码参数")
//                      @NotBlank(message = "没有提供新密码参数") String newPassword) {
//        UserModel userModel = this.userMapper.selectById(userId);
//        if (userModel.isInitPassword() && !StringUtils.hasText(currentPassword)) {
//            throw new IllegalArgumentException("当前密码错误，重置密码失败！");
//        }
//
//        // 判断当前密码是否正确
//        if (userModel.isInitPassword() && !userModel.getPassword().equals(currentPassword)) {
//            throw new IllegalArgumentException("当前密码错误，重置密码失败！");
//        }
//
//        userModel.setPassword(newPassword);
//        userModel.setInitPassword(true);
//        userModel.setResetPasswordTime(new Date());
//        this.userMapper.updateById(userModel);
//    }
//
//    /**
//     * 找回密码的重置密码
//     *
//     * @param secretIdentifier
//     * @param newPassword
//     */
//    public void resetForget(@NotNull(message = "没有提供secretIdentifier参数")
//                            @NotBlank(message = "没有提供secretIdentifier参数") String secretIdentifier,
//                            @NotNull(message = "没有提供新密码参数")
//                            @NotBlank(message = "没有提供新密码参数") String newPassword) throws BusinessException {
//        if (!this.redisTemplate.hasKey(secretIdentifier)) {
//            log.debug("提供的secretIdentifier {} 不存在，有可能是恶意攻击导致的此日志产生", secretIdentifier);
//            throw new BusinessException("secretIdentifier " + secretIdentifier + " 错误");
//        }
//
//        String email = this.redisTemplate.opsForValue().get(secretIdentifier);
//        UpdateWrapper<UserModel> updateWrapper = Wrappers.update();
//        updateWrapper.eq("email", email);
//        updateWrapper.set("password", newPassword);
//        this.userMapper.update(null, updateWrapper);
//    }
//
//}
