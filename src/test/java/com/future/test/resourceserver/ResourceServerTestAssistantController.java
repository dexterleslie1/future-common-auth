package com.future.test.resourceserver;


import com.future.common.auth.dto.UserDTO;
import com.future.common.auth.security.CustomizeAuthentication;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "辅助测试ResourceServer接口")
@RestController
@RequestMapping(value = "/api/v1/future/auth")
@Slf4j
public class ResourceServerTestAssistantController {
    @ApiOperation(value = "辅助测试ResourceServer接口，提供 Access Token 返回当前用户ID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = HttpHeaders.AUTHORIZATION, value = "Access Token", required = true, dataType = "String", paramType = "header"),
    })
    @GetMapping("resourceServerTestAssistant")
    public ObjectResponse<UserDTO> resourceServerTestAssistant(CustomizeAuthentication authentication) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(authentication.getUser().getUserId());
        return ResponseUtils.successObject(userDTO);
    }
}
