package com.future.test.resourceserver;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ResourceServerTestConfig.class})
@ActiveProfiles("resourceserver")
@AutoConfigureMockMvc
public class ResourceServerTests {
    @Resource
    MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzUxMiJ9.eyJ0b2tlblR5cGUiOiJBY2Nlc3MiLCJleHAiOjE3NDE2Njg2NDcsInVzZXJJZCI6MTIsImlhdCI6MTc0MTY2MTQ0N30.StbDTPh0b-Fdqv1es_EX2NIs3AH66vkxaH48kZKhzJYUc09pNxcuFQYgdvySBL7PlK64isLpZPBzHao0qkKK3ClHUHxdyRvJlNtBQgUKRjSW757FlOWcEVVSrXekKu-k5a1jb-j9zmxd15h2CiRz5uk0P8kD693o3Q00F08jV-s";
        Integer userId = 12;
        // 获取用户信息
        ResultActions resultActions = this.mockMvc.perform(get("/api/v1/future/auth/resourceServerTestAssistant")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk());
        String JSON = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertEquals(userId, JsonPath.read(JSON, "$.data.id"));
    }
}
