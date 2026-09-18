package com.dine.oss;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public String fileUrl(String storedPath) {
        return OssPathUtil.toUrl(getDomain(), storedPath);
    }

    @Override
    public String upload(File file) {
        try (InputStream in = new FileInputStream(file)) {
            return upload(in, file.length(), file.getName());
        } catch (IOException e) {
            throw new IllegalStateException("上传失败，请检查对象存储配置及权限", e);
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            return upload(in, file.getSize(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new IllegalStateException("上传失败，请检查对象存储配置及权限", e);
        }
    }

    @Override
    public String upload(File file, String objectPath) {
        try (InputStream in = new FileInputStream(file)) {
            return put(in, file.length(), objectPath);
        } catch (IOException e) {
            throw new IllegalStateException("上传失败，请检查对象存储配置及权限", e);
        }
    }

    @Override
    public String upload(InputStream inputStream, long contentLength, String fileName) {
        String relative = LocalDate.now().format(DAY) + "/" + UUID.randomUUID().toString().replace("-", "")
                + OssPathUtil.extension(fileName);
        return put(inputStream, contentLength, relative);
    }

    @Override
    public List<String> list(String subDir) {
        List<String> result = new ArrayList<>();
        String prefix = folder() + "/" + OssPathUtil.stripSlash(subDir);
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(properties.getBucketName())
                    .prefix(prefix)
                    .build());
            for (S3Object object : response.contents()) {
                if (object.key() != null && !object.key().endsWith("/")) {
                    result.add("/" + object.key());
                }
            }
        } catch (Exception e) {
            logger.error("列出对象存储文件失败, prefix={}", prefix, e);
        }
        return result;
    }

    @Override
    public File materialize(String storedPath) {
        if (StrUtil.isBlank(storedPath)) {
            return null;
        }
        String key = OssPathUtil.stripSlash(storedPath);
        String name = key.substring(key.lastIndexOf('/') + 1);
        File temp = new File(System.getProperty("java.io.tmpdir"),
                "dinenova-" + UUID.randomUUID().toString().replace("-", "") + "-" + name);
        try {
            s3Client.getObject(GetObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build(), temp.toPath());
            return temp;
        } catch (Exception e) {
            logger.error("下载对象存储文件失败, key={}", key, e);
            return null;
        }
    }

    @PreDestroy
    public void close() {
        s3Client.close();
    }

    private String put(InputStream inputStream, long contentLength, String objectPath) {
        String key = folder() + "/" + OssPathUtil.stripSlash(objectPath);
        try {
            RequestBody body = contentLength >= 0
                    ? RequestBody.fromInputStream(inputStream, contentLength)
                    : RequestBody.fromBytes(inputStream.readAllBytes());
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build(), body);
            logger.info("上传对象存储成功, type={}, key={}", properties.getType(), key);
            return "/" + key;
        } catch (Exception e) {
            logger.error("上传对象存储失败, type={}", properties.getType(), e);
            throw new IllegalStateException("上传失败，请检查对象存储配置及权限", e);
        }
    }

    private String folder() {
        return OssPathUtil.stripSlash(StrUtil.blankToDefault(properties.getFolder(), "uploads"));
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
