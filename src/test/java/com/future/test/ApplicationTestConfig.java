package com.future.test;

import com.future.common.auth.EnableFutureAuthorization;
import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFutureExceptionHandler
@EnableFutureAuthorization
public class ApplicationTestConfig {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTestConfig.class, args);
    }
}
