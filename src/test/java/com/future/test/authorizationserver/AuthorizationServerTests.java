package com.future.test.authorizationserver;

import com.future.common.auth.service.VerificationCodeService;
import com.future.common.phone.RandomPhoneGeneratorUtil;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {AuthorizationServerTestConfig.class})
@ActiveProfiles("authorizationserver")
@AutoConfigureMockMvc
@Slf4j
public class AuthorizationServerTests {
    @Resource
    MockMvc mockMvc;
    @SpyBean
    VerificationCodeService verificationCodeService;

    @Test
    public void test() throws Exception {
        // region 测试使用手机注册和登录
        {
            // 随机生成手机号码
            String verificationCode = RandomStringUtils.randomNumeric(4);
            String phone = RandomPhoneGeneratorUtil.getRandom();
            Mockito.doReturn(verificationCode).when(this.verificationCodeService).generateRandomCode();
            ResultActions resultActions = this.mockMvc.perform(get("/api/v1/future/auth/verificationCode/get")
                    .queryParam("phoneOrEmail", phone));
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is(300)));

            // 使用手机注册
            resultActions = this.mockMvc.perform(post("/api/v1/future/auth/registerWithVerificationCode")
                    .queryParam("phoneOrEmail", phone)
                    .queryParam("password", "123456")
                    .queryParam("verificationCode", verificationCode));
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is("注册成功")));

            // 使用手机登录
            resultActions = this.mockMvc.perform(post("/api/v1/future/auth/loginWithPassword")
                            .queryParam("loginNameOrPhoneOrEmail", phone)
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk());
            String JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Integer userId = JsonPath.read(JSON, "$.data.userId");
            String refreshToken = JsonPath.read(JSON, "$.data.refreshToken");
            String accessToken = JsonPath.read(JSON, "$.data.accessToken");
            Assertions.assertEquals(phone, JsonPath.read(JSON, "$.data.phone"));

            // 获取用户信息
            resultActions = this.mockMvc.perform(get("/api/v1/future/auth/getSelfInfo")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk());
            JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Assertions.assertEquals(userId, JsonPath.read(JSON, "$.data.id"));
            Assertions.assertEquals(phone, JsonPath.read(JSON, "$.data.phone"));
        }

        // endregion

        // region 测试使用帐号注册和登录
        {
            String loginName = RandomStringUtils.randomAlphanumeric(20);

            // 在注册时，用于设置帐号是否已经存在 UI 提示标记
            this.mockMvc.perform(get("/api/v1/future/auth/checkIfLoginNameExists")
                            .queryParam("loginName", loginName))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is(false)));

            // 不提供帐号
            this.mockMvc.perform(post("/api/v1/future/auth/registerWithLoginName")
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errorMessage", is("Missing required parameter \"loginName\"!")));
            this.mockMvc.perform(post("/api/v1/future/auth/registerWithLoginName")
                            .queryParam("loginName", "")
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errorMessage", is("请提供注册帐号")));
            this.mockMvc.perform(post("/api/v1/future/auth/registerWithLoginName")
                            .queryParam("loginName", "          ")
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.errorMessage", is("请提供注册帐号")));

            this.mockMvc.perform(post("/api/v1/future/auth/registerWithLoginName")
                            .queryParam("loginName", loginName)
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is("注册成功")));

            // 在注册时，用于设置帐号是否已经存在 UI 提示标记
            this.mockMvc.perform(get("/api/v1/future/auth/checkIfLoginNameExists")
                            .queryParam("loginName", loginName))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is(true)));

            // 用户登录
            ResultActions resultActions = this.mockMvc.perform(post("/api/v1/future/auth/loginWithPassword")
                            .queryParam("loginNameOrPhoneOrEmail", loginName)
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk());
            String JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Integer userId = JsonPath.read(JSON, "$.data.userId");
            String refreshToken = JsonPath.read(JSON, "$.data.refreshToken");
            String accessToken = JsonPath.read(JSON, "$.data.accessToken");
            Assertions.assertEquals(loginName, JsonPath.read(JSON, "$.data.loginName"));

            // 获取用户信息
            resultActions = this.mockMvc.perform(get("/api/v1/future/auth/getSelfInfo")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk());
            JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Assertions.assertEquals(userId, JsonPath.read(JSON, "$.data.id"));
            Assertions.assertEquals(loginName, JsonPath.read(JSON, "$.data.loginName"));
        }
        // endregion

        String accessTokenAssistant = null;
        // region 测试使用email注册和登录
        {
            // 随机生成手机号码
            String verificationCode = RandomStringUtils.randomNumeric(4);
            String email = RandomStringUtils.randomAlphanumeric(20) + "@qq.com";
            Mockito.doReturn(verificationCode).when(this.verificationCodeService).generateRandomCode();
            ResultActions resultActions = this.mockMvc.perform(get("/api/v1/future/auth/verificationCode/get")
                    .queryParam("phoneOrEmail", email));
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is(300)));

            // 使用手机注册
            resultActions = this.mockMvc.perform(post("/api/v1/future/auth/registerWithVerificationCode")
                    .queryParam("phoneOrEmail", email)
                    .queryParam("password", "123456")
                    .queryParam("verificationCode", verificationCode));
            resultActions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", is("注册成功")));

            // 使用手机登录
            resultActions = this.mockMvc.perform(post("/api/v1/future/auth/loginWithPassword")
                            .queryParam("loginNameOrPhoneOrEmail", email)
                            .queryParam("password", "123456"))
                    .andExpect(status().isOk());
            String JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Integer userId = JsonPath.read(JSON, "$.data.userId");
            String refreshToken = JsonPath.read(JSON, "$.data.refreshToken");
            String accessToken = JsonPath.read(JSON, "$.data.accessToken");
            Assertions.assertEquals(email, JsonPath.read(JSON, "$.data.email"));

            // 获取用户信息
            resultActions = this.mockMvc.perform(get("/api/v1/future/auth/getSelfInfo")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                    .andExpect(status().isOk());
            JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            Assertions.assertEquals(userId, JsonPath.read(JSON, "$.data.id"));
            Assertions.assertEquals(email, JsonPath.read(JSON, "$.data.email"));

            accessTokenAssistant = accessToken;
        }
        // endregion

        // 用于协助测试 ResourceServer，打印一个 JWT Access Token，需要手动复制到 ResourceServerTests 中
        log.debug("JWT Access Token: {}", accessTokenAssistant);
    }
}
