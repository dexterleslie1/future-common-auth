//package com.future.common.auth.controller;
//
//import com.future.auth.service.EmailService;
//import com.future.auth.service.ResetPasswordService;
//import com.future.common.auth.security.CustomizeAuthentication;
//import com.future.common.exception.BusinessException;
//import com.future.common.http.ObjectResponse;
//import com.future.common.http.ResponseUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class ResetPasswordController {
//
//    @Resource
//    ResetPasswordService resetPasswordService;
//    @Resource
//    EmailService emailService;
//
//
//    /**
//     * 发送重置密码链接到邮箱
//     *
//     * @param email
//     * @return
//     */
//    @PostMapping("sendResetPasswordLinkToEmail")
//    public ObjectResponse<String> sendResetPasswordLinkToEmail(
//            @RequestParam(value = "email", defaultValue = "") String email) throws BusinessException {
//        this.emailService.sendResetPasswordLink(email);
//        return ResponseUtils.successObject("成功发送重置密码邮件到邮箱 " + email + "中，请登录您的邮箱点击重置密码链接修改您的密码");
//    }
//
//    /**
//     * 找回密码功能的重置密码
//     *
//     * @param secretIdentifier
//     * @param newPassword
//     * @return
//     */
//    @PutMapping("resetPasswordForget")
//    public ObjectResponse<String> resetPasswordForget(
//            @RequestParam(value = "secretIdentifier", defaultValue = "") String secretIdentifier,
//            @RequestParam(value = "newPassword", defaultValue = "") String newPassword) throws BusinessException {
//        this.resetPasswordService.resetForget(secretIdentifier, newPassword);
//        ObjectResponse<String> response = new ObjectResponse<>();
//        response.setData("成功重置密码");
//        return response;
//    }
//
//    /**
//     * 用户登录后的重置密码
//     *
//     * @param currentPassword
//     * @param newPassword
//     * @return
//     */
//    @PutMapping("resetPassword")
//    public ObjectResponse<String> resetPassword(
//            @RequestParam(value = "currentPassword", defaultValue = "") String currentPassword,
//            @RequestParam(value = "newPassword", defaultValue = "") String newPassword,
//            CustomizeAuthentication customizeAuthentication) {
//        Long userId = customizeAuthentication.getUser().getUserId();
//        this.resetPasswordService.reset(userId, currentPassword, newPassword);
//        ObjectResponse<String> response = new ObjectResponse<>();
//        response.setData("成功重置密码");
//        return response;
//    }
//
//}
