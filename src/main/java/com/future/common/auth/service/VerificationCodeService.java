package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.future.common.exception.BusinessException;
import com.future.common.phone.PhoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * 验证码相关业务
 */
// todo 代码格式化更加好看
@Service
@Slf4j
public class VerificationCodeService {
    public final static String VerificationCodeKeyPrefix = "vc#";
    /**
     * 验证码校验失败计数器缓存前缀
     */
    public final static String VerificationCodeVerifyFailCounterPrefix = "vcvfc#";
    /**
     * 验证码缓存失效时间为300秒
     */
    public final static int VerificationCodeTTLInSeconds = 300;
    /**
     * 验证码校验失败计数器缓存失效时间秒数
     */
    public final static int VerificationCodeVerifyFailCounterTTLInSeconds = VerificationCodeTTLInSeconds + 30;

    @Autowired
    SmsService smsService = null;

    @Autowired
    RedisTemplate<String, String> redisTemplate = null;

    EmailValidator emailValidator = EmailValidator.getInstance();

    /**
     * 根据 手机号码/Email 发送验证码
     * todo 考虑如何优雅实现短信攻击拦截逻辑
     *
     * @param phoneOrEmail 手机号或者邮箱
     * @return 验证码过期秒数
     */
    public int get(@NotNull(message = "请提供手机号或者邮箱参数")
                   @NotBlank(message = "请提供手机号或者邮箱参数") String phoneOrEmail) {
        verifyPhoneOrEmail(phoneOrEmail);

        String key = VerificationCodeKeyPrefix + phoneOrEmail;

        String verificationCode = this.generateRandomCode();
        if (log.isDebugEnabled()) {
            log.debug("随机生成 手机号码/邮箱 {} 对应的验证码 {}", phoneOrEmail, verificationCode);
        }

        int ttlInSeconds = this.getTimeoutInSeconds();
        // todo 这里逻辑会发生验证码续命漏洞
        this.redisTemplate.opsForValue().set(key,
                verificationCode,
                ttlInSeconds,
                TimeUnit.SECONDS);
        if (log.isDebugEnabled()) {
            log.debug("成功保存 手机号码/Email {} 对应的验证码 {} 到缓存中", phoneOrEmail, verificationCode);
        }

        try {
            PhoneUtil.isPhone(phoneOrEmail);
            this.smsService.send(phoneOrEmail, verificationCode);
        } catch (IllegalArgumentException ignored) {
            // 非手机号码
            // todo 调用email服务发送邮件
        }

        return ttlInSeconds;
    }

    /**
     * 生成随机验证码，把这个逻辑独立出来是为了测试时候支持此方法的mock
     *
     * @return
     */
    public String generateRandomCode() {
        return RandomStringUtils.randomNumeric(4);
    }

    /**
     * 获取验证码过期秒数，把这个方法独立是为了方便mock测试
     *
     * @return
     */
    public int getTimeoutInSeconds() {
        return VerificationCodeTTLInSeconds;
    }

    /**
     * 校验验证码是否正确
     * <p>
     * todo 瞬时并发校验漏洞
     *
     * @param phoneOrEmail
     * @param verificationCode
     */
    void verify(String phoneOrEmail, String verificationCode) throws BusinessException {
        Assert.isTrue(!StringUtils.isBlank(phoneOrEmail), "请指定 手机号码/Email");
        Assert.isTrue(!StringUtils.isBlank(verificationCode), "请指定验证码");

        verifyPhoneOrEmail(phoneOrEmail);

        String key = VerificationCodeKeyPrefix + phoneOrEmail;

        String verificationCodeInCache = this.redisTemplate.opsForValue().get(key);

        if (log.isDebugEnabled() && StringUtils.isBlank(verificationCodeInCache)) {
            log.debug("手机号码/Email {} 没有对应的验证码在缓存中", phoneOrEmail);
        }

        Assert.isTrue(!StringUtils.isBlank(verificationCodeInCache), "验证码过期，请重新获取");

        if (!verificationCodeInCache.equals(verificationCode)) {
            String keyVerifyFailCounter = VerificationCodeVerifyFailCounterPrefix + phoneOrEmail;
            Long verifyFailCounter =
                    this.redisTemplate.opsForValue().increment(keyVerifyFailCounter, 1);
            // 校验失败大于等于3次后从缓存中删除验证码
            // 以防止暴力破解验证码
            if (verifyFailCounter != null && verifyFailCounter >= 3) {
                // todo 考虑是否有必要实现下面逻辑的原子性
                this.redisTemplate.delete(key);
                this.redisTemplate.delete(keyVerifyFailCounter);
            } else {
                // 设置验证码校验失败计数器的过期时间
                this.redisTemplate.expire(keyVerifyFailCounter, VerificationCodeVerifyFailCounterTTLInSeconds, TimeUnit.SECONDS);
            }

            if (log.isDebugEnabled()) {
                log.debug("手机号码/Email {} 提供的验证码 {} 和缓存中的验证码 {} 不匹配，校验失败第 {} 次",
                        phoneOrEmail, verificationCode, verificationCodeInCache, verifyFailCounter);
            }

            throw new BusinessException("您提供的验证码错误！");
        }

        if (log.isDebugEnabled()) {
            log.debug("手机号码/Email {} 验证码校验通过", phoneOrEmail);
        }
    }

    void verifyPhoneOrEmail(String phoneOrEmail) {
        try {
            PhoneUtil.isPhone(phoneOrEmail);
        } catch (IllegalArgumentException ignored) {
            // 如果不是手机
            boolean isEmail = this.emailValidator.isValid(phoneOrEmail);
            if (!isEmail) {
                throw new IllegalArgumentException("提供的参数 " + phoneOrEmail + " 不是手机号或者邮箱格式");
            }
        }
    }
}
