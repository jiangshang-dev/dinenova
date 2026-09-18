package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.oss.FileStorageService;
import com.dine.service.SettingService;
import com.dine.util.CommonUtil;
import com.dine.util.DateUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传管理控制类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-文件上传相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/file")
public class ClientFileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientFileController.class);

    /**
     * 系统环境变量
     * */
    private Environment env;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 文件存储
     * */
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     *
     * @param request
     * @throws IOException
     */
    @Operation(summary = "上传文件")
    @Debounce
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String sourcePic = request.getParameter("sourcePic");
        MultipartFile file = multipartRequest.getFile(sourcePic);

        if (file == null) {
            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap.size() > 0) {
                file = fileMap.get("file");
            }
        }

        Map<String, String> resultMap = new HashMap<>();
        String originalFilename = file.getOriginalFilename();
        if (StringUtil.isEmpty(originalFilename)) {
            return getFailureResult(201, "上传出错啦");
        }

        String maxSizeStr = env.getProperty("images.upload.maxSize");

        // 默认限制2M
        float maxSize = 2;
        try {
            maxSize = Float.parseFloat(maxSizeStr);
        } catch (NumberFormatException e) {
            logger.error("图片允许的大小设置不正确", e);
        }
        if (file.getSize() > (maxSize * 1024 * 1024)) {
            return getFailureResult(201, "上传的图片不能大于" + maxSize + "MB");
        }

        String fileType = file.getContentType();
        if (fileType.indexOf("image") == -1) {
            return getFailureResult(201, "上传的图片格式有误");
        }

        String original = file.getOriginalFilename().toLowerCase();
        if (original.indexOf("jpg") == -1 && original.indexOf("jpeg") == -1 && original.indexOf("png") == -1 && original.indexOf("gif") == -1 && original.indexOf("bmp") == -1) {
            return getFailureResult(201, "上传的图片格式有误");
        }

        // 保存文件
        try {
            String fileName = saveFile(file);
            String baseImage = settingService.getUploadBasePath();
            String filePath = baseImage + fileName;
            String url = filePath;

            // 按配置上传到对象存储，本地模式保持原路径
            if (fileStorageService.isRemote()) {
                String pathRoot = env.getProperty("images.root");
                if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
                    pathRoot = ResourceUtils.getURL("classpath:").getPath();
                }
                File ossFile = new File(pathRoot + fileName);
                fileName = fileStorageService.upload(ossFile);
                filePath = fileStorageService.getDomain() + fileName;
                url = filePath;
            }
            
            resultMap.put("status", "success");
            resultMap.put("domain", baseImage);
            resultMap.put("filePath", filePath);
            resultMap.put("fileName", fileName);
            resultMap.put("state", "SUCCESS");
            resultMap.put("original", file.getOriginalFilename());
            resultMap.put("size", file.getSize()+"");
            resultMap.put("title", fileName);
            resultMap.put("type", file.getContentType());
            resultMap.put("url", url);
        } catch (Exception e) {
            return getFailureResult(201, "上传失败，请检查上传配置及权限");
        }

        return getSuccessResult(resultMap);
    }

    public String saveFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String imageName = fileName.substring(fileName.lastIndexOf("."));
        String pathRoot = env.getProperty("images.root");
        if (pathRoot == null || StringUtil.isEmpty(pathRoot)) {
            pathRoot = ResourceUtils.getURL("classpath:").getPath();
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String baseImage = env.getProperty("images.path");
        String filePath = baseImage + DateUtil.formatDate(new Date(), "yyyyMMdd") + "/";
        String path = filePath + uuid + imageName;
        try {
            File tempFile = new File(pathRoot + path);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            CommonUtil.saveMultipartFile(file, pathRoot + path);
        } catch (Exception e) {
            throw new Exception("上传失败，请检查目录是否可写");
        }
        return path;
    }
}
