package com.future.common.auth.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// 加载mybatis-plus properties默认配置
@PropertySource("classpath:default-config-for-mybatis-plus.properties")
public class ConfigMyBatisPlus {
    /**
     * mybatis-plus分页插件
     * 注意：要使用mybatis-plus分页功能必须配置MybatisPlusInterceptor，否则无法使用mybatis-plus分页功能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
