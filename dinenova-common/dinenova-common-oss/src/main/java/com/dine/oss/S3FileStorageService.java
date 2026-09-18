package com.dine.oss;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 基于 AWS S3 SDK 的存储实现，兼容阿里云 OSS、MinIO、RustFS
 */
public class S3FileStorageService implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3FileStorageService.class);

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OssProperties properties;

    private final S3Client s3Client;

    public S3FileStorageService(OssProperties properties) {
        this.properties = properties;
        String endpoint = normalizeEndpoint(properties.getEndpoint());
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .region(Region.of(resolveRegion(endpoint)))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(properties.isPathStyle())
                        .chunkedEncodingEnabled(false)
                        .build())
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .httpClient(UrlConnectionHttpClient.create())
                .build();
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public String getDomain() {
        if (StrUtil.isNotBlank(properties.getDomain())) {
            return trimSlash(properties.getDomain());
        }
        String endpoint = normalizeEndpoint(properties.getEndpoint());
        if (properties.isPathStyle()) {
            return trimSlash(endpoint) + "/" + properties.getBucketName();
        }
        URI uri = URI.create(endpoint);
        String scheme = StrUtil.blankToDefault(uri.getScheme(), "https");
        return scheme + "://" + properties.getBucketName() + "." + uri.getHost();
    }

    @Override
    public String upload(File file) {
        String folder = StrUtil.blankToDefault(properties.getFolder(), "uploads");
        String key = folder + "/" + LocalDate.now().format(DAY) + "/" + file.getName();
        try {
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build(), software.amazon.awssdk.core.sync.RequestBody.fromFile(file));
            logger.info("上传对象存储成功, type={}, key={}", properties.getType(), key);
            return "/" + key;
        } catch (Exception e) {
            logger.error("上传对象存储失败, type={}", properties.getType(), e);
            throw new IllegalStateException("上传失败，请检查对象存储配置及权限", e);
        }
    }

    @PreDestroy
    public void close() {
        s3Client.close();
    }

    private String resolveRegion(String endpoint) {
        if (StrUtil.isNotBlank(properties.getRegion())) {
            return properties.getRegion();
        }
        String host = endpoint.replace("https://", "").replace("http://", "");
        if (host.startsWith("oss-")) {
            String region = host.substring(4);
            int dot = region.indexOf('.');
            if (dot > 0) {
                return region.substring(0, dot);
            }
        }
        return "us-east-1";
    }

    private String normalizeEndpoint(String endpoint) {
        if (StrUtil.isBlank(endpoint)) {
            throw new IllegalStateException("未配置 dinenova.oss.endpoint");
        }
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            return trimSlash(endpoint);
        }
        return "https://" + trimSlash(endpoint);
    }

    private String trimSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
