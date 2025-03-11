package com.future.test.authorizationserver;

import com.future.common.auth.EnableFutureAuthorizationServer;
import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFutureExceptionHandler
@EnableFutureAuthorizationServer
public class AuthorizationServerTestConfig {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerTestConfig.class, args);
    }
}
