package com.future.common.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@PropertySource("classpath:default-config-for-swagger.properties")
public class ConfigSwagger {
    /**
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 隐藏默认Http code
                // https://github.com/springfox/springfox/issues/632
                .useDefaultResponseMessages(false)
                .apiInfo(createApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.future.common.auth"))//扫描com路径下的api文档
                .paths(PathSelectors.any())//路径判断
                .build();
    }

    /**
     * @return
     */
    private ApiInfo createApiInfo() {
        return new ApiInfoBuilder()
                .title("Future系统Auth模块")//标题
                .description("Future系统Auth模块")
                .version("1.0.0")//版本号
                .build();
    }
}
