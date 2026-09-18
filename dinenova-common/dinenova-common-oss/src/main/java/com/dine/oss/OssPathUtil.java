package com.dine.oss;

import cn.hutool.core.util.StrUtil;

final class OssPathUtil {

    private OssPathUtil() {
    }

    static String stripSlash(String value) {
        if (value == null) {
            return "";
        }
        String result = value;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    static String extension(String fileName) {
        if (StrUtil.isBlank(fileName) || fileName.lastIndexOf('.') < 0) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    static String toUrl(String domain, String storedPath) {
        if (StrUtil.isBlank(storedPath)) {
            return storedPath;
        }
        if (storedPath.startsWith("http://") || storedPath.startsWith("https://") || storedPath.startsWith("data:")) {
            return storedPath;
        }
        String path = storedPath.startsWith("/") ? storedPath : "/" + storedPath;
        if (StrUtil.isBlank(domain)) {
            return path;
        }
        String host = domain.endsWith("/") ? domain.substring(0, domain.length() - 1) : domain;
        return host + path;
    }
}
