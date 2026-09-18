package com.dine.oss;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 按 dinenova.oss.type 选择存储实现，对象存储统一走 S3 协议
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    @Bean
    public FileStorageService fileStorageService(OssProperties properties, Environment environment) {
        if (properties.isLocal()) {
            return new LocalFileStorageService(environment);
        }
        return new S3FileStorageService(properties);
    }
}
