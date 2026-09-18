package com.dine.oss;

import org.springframework.core.env.Environment;

import java.io.File;

/**
 * 本地磁盘存储
 */
public class LocalFileStorageService implements FileStorageService {

    private final Environment environment;

    public LocalFileStorageService(Environment environment) {
        this.environment = environment;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public String getDomain() {
        String url = environment.getProperty("images.upload.url", "");
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    @Override
    public String upload(File file) {
        String baseImage = environment.getProperty("images.path", "/static/uploadImages/");
        if (!baseImage.endsWith("/")) {
            baseImage = baseImage + "/";
        }
        return baseImage + file.getName();
    }
}
