package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.future.common.phone.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 短信相关业务
 */
@Service
@Slf4j
public class SmsService {
    /**
     * 发送短信到手机
     *
     * @param phoneNumber
     * @param verificationCode
     */
    public void send(String phoneNumber, String verificationCode) {
        Assert.isTrue(!StringUtils.isBlank(phoneNumber),
                "请指定手机号码");

        PhoneUtil.isPhone(phoneNumber);

        // 调用第三方短信网关发送短信

        log.debug("成功调用第三方短信网关发送验证码 {} 到手机号码 {}",
                verificationCode,
                phoneNumber);
    }
}
