package com.dine.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置。阿里云 OSS、MinIO、RustFS 都走 S3 协议，本地使用磁盘。
 */
@Data
@ConfigurationProperties(prefix = "dinenova.oss")
public class OssProperties {

    /**
     * 存储类型，可选 local、aliyun、minio、rustfs、s3。未配置时默认 s3
     */
    private String type = "s3";

    private String accessKey;

    private String secretKey;

    private String endpoint;

    private String bucketName;

    /**
     * 对象存储目录
     */
    private String folder = "uploads";

    /**
     * type=local 时的磁盘目录，必须配置，不再默认写用户目录
     */
    private String localPath;

    /**
     * 访问域名，不配置时按 endpoint 拼接
     */
    private String domain;

    /**
     * 区域，不配置时从 endpoint 推断
     */
    private String region;

    public boolean isLocal() {
        return "local".equalsIgnoreCase(type);
    }

    /**
     * MinIO、RustFS、通用 S3 使用路径风格；阿里云使用虚拟主机风格
     */
    public boolean isPathStyle() {
        return !"aliyun".equalsIgnoreCase(type);
    }
}
