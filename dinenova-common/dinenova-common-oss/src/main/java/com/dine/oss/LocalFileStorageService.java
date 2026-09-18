package com.dine.oss;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 本地磁盘存储。目录读 dinenova.oss.local-path，域名读 dinenova.oss.domain。
 */
public class LocalFileStorageService implements FileStorageService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final OssProperties properties;

    public LocalFileStorageService(OssProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getDomain() {
        return trimSlash(StrUtil.blankToDefault(properties.getDomain(), ""));
    }

    @Override
    public String fileUrl(String storedPath) {
        return OssPathUtil.toUrl(getDomain(), storedPath);
    }

    @Override
    public String upload(File file) {
        return putFile(file, datedName(file.getName()));
    }

    @Override
    public String upload(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            return upload(in, file.getSize(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new IllegalStateException("上传失败，请检查本地目录是否可写", e);
        }
    }

    @Override
    public String upload(File file, String objectPath) {
        return putFile(file, objectPath);
    }

    @Override
    public String upload(InputStream inputStream, long contentLength, String fileName) {
        String relative = datedName(fileName);
        File target = new File(root(), OssPathUtil.stripSlash(folder() + "/" + relative));
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("上传失败，请检查本地目录是否可写");
        }
        try {
            Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("上传失败，请检查本地目录是否可写", e);
        }
        return "/" + folder() + "/" + relative;
    }

    @Override
    public List<String> list(String subDir) {
        List<String> result = new ArrayList<>();
        File dir = new File(root(), OssPathUtil.stripSlash(folder() + "/" + StrUtil.blankToDefault(subDir, "")));
        File[] files = dir.listFiles();
        if (files == null) {
            return result;
        }
        for (File file : files) {
            if (file.isFile()) {
                result.add("/" + folder() + "/" + OssPathUtil.stripSlash(subDir) + "/" + file.getName());
            }
        }
        return result;
    }

    @Override
    public File materialize(String storedPath) {
        if (StrUtil.isBlank(storedPath)) {
            return null;
        }
        File file = new File(root(), OssPathUtil.stripSlash(storedPath));
        return file.isFile() ? file : null;
    }

    private String putFile(File file, String objectPath) {
        String relative = OssPathUtil.stripSlash(objectPath);
        File target = new File(root(), OssPathUtil.stripSlash(folder() + "/" + relative));
        File parent = target.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("上传失败，请检查本地目录是否可写");
        }
        try {
            Files.copy(file.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("上传失败，请检查本地目录是否可写", e);
        }
        return "/" + folder() + "/" + relative;
    }

    private String datedName(String fileName) {
        String ext = OssPathUtil.extension(fileName);
        return LocalDate.now().format(DAY) + "/" + UUID.randomUUID().toString().replace("-", "") + ext;
    }

    private String folder() {
        return OssPathUtil.stripSlash(StrUtil.blankToDefault(properties.getFolder(), "uploads"));
    }

    private File root() {
        if (StrUtil.isBlank(properties.getLocalPath())) {
            throw new IllegalStateException("未配置 dinenova.oss.local-path");
        }
        return new File(properties.getLocalPath());
    }

    private String trimSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }
}
