package com.dine.oss;

import java.io.File;

/**
 * 文件存储，按 dinenova.oss.type 切换本地或 S3 兼容存储
 */
public interface FileStorageService {

    /**
     * 是否走远程对象存储
     */
    boolean isRemote();

    /**
     * 访问域名，不含结尾斜杠
     */
    String getDomain();

    /**
     * 上传文件，返回以 / 开头的相对路径
     */
    String upload(File file);
}
