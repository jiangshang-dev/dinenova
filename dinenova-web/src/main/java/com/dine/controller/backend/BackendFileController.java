package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.oss.FileStorageService;
import com.dine.service.SettingService;
import com.dine.util.CommonUtil;
import com.dine.util.DateUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
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
@Tag(name = "管理端-文件上传相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/file")
public class BackendFileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendFileController.class);

    /**
     * 环境变量
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
     * 后台上传文件
     *
     * @param request
     * @throws
     */
    @Operation(summary = "后台上传文件")
    @Debounce
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject uploadFileLocal(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        String action = request.getParameter("action") == null ? "" : request.getParameter("action");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (action.equals("config")) {
            Map<String, Object> outParams = new HashMap();
            outParams.put("imageActionName", "upload");
            outParams.put("fileActionName", "upload");
            outParams.put("fileFieldName", "file");
            outParams.put("imageFieldName", "file");
            outParams.put("fileUrlPrefix", "");
            outParams.put("imageUrlPrefix", "");
            return getSuccessResult(outParams);
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String sourcePic = request.getParameter("sourcePic");// 页面图片元素的ID
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
            return getFailureResult(201, "上传的文件不能大于" + maxSize + "MB");
        }

        String fileType = file.getContentType();

        // 保存文件
        try {
            String fileName = saveFile(file);
            String baseImage = settingService.getUploadBasePath();
            String filePath = baseImage + fileName;
            String url = filePath;

            // 按配置上传到对象存储，本地模式保持原路径
            if (fileStorageService.isRemote() && fileType != null && fileType.indexOf("image") >= 0) {
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
        String filePath = baseImage + DateUtil.formatDate(new Date(), "yyyyMMdd")+"/";

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
