package com.dine.oss;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 按 dinenova.oss.type 选择存储实现，对象存储统一走 S3 协议
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    @Bean
    public FileStorageService fileStorageService(OssProperties properties) {
        if (properties.isLocal()) {
            return new LocalFileStorageService(properties);
        }
        return new S3FileStorageService(properties);
    }
}
