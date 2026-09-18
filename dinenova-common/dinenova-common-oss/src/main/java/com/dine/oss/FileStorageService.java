package com.dine.oss;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 文件存储。落盘位置只由 dinenova.oss.type 决定：local / aliyun / minio / rustfs。
 */
public interface FileStorageService {

    /**
     * 访问域名，不含结尾斜杠
     */
    String getDomain();

    /**
     * 拼出可访问地址。已经是 http/data 则原样返回。
     */
    String fileUrl(String storedPath);

    /**
     * 按日期目录上传，返回以 / 开头的存储路径
     */
    String upload(File file);

    /**
     * 按日期目录上传
     */
    String upload(MultipartFile file);

    /**
     * 上传到指定相对路径（相对 dinenova.oss.folder），如 qrcode/1.png
     */
    String upload(File file, String objectPath);

    /**
     * 流上传
     */
    String upload(InputStream inputStream, long contentLength, String fileName);

    /**
     * 列出 folder 下指定子目录的对象路径，返回带 / 的存储路径
     */
    List<String> list(String subDir);

    /**
     * 把已保存的相对路径落到临时文件，给需要本地 File 的处理使用
     */
    File materialize(String storedPath);
}
