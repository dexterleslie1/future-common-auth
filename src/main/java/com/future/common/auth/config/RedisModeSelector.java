package com.future.common.auth.config;

import com.future.common.auth.EnableFutureAuthorization;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.text.MessageFormat;
import java.util.Map;

public class RedisModeSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 获取 @EnableMyCache 缓存类型注解值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableFutureAuthorization.class.getName());
        RedisMode redisMode = (RedisMode) annotationAttributes.get("redisMode");
        switch (redisMode) {
            case Standalone: {
                return new String[]{ConfigRedisStandalone.class.getName()};
            }
            case Cluster: {
                return new String[]{ConfigRedisCluster.class.getName()};
            }
            default: {
                throw new RuntimeException(MessageFormat.format("不受支持的Redis模式 {0}", redisMode.toString()));
            }
        }
    }
}