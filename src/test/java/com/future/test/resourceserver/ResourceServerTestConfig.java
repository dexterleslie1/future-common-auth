package com.future.test.resourceserver;

import com.future.common.auth.EnableFutureResourceServer;
import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFutureExceptionHandler
@EnableFutureResourceServer
@Import(ResourceServerTestAssistantController.class)
public class ResourceServerTestConfig {
    public static void main(String[] args) {
        SpringApplication.run(ResourceServerTestConfig.class, args);
    }
}
