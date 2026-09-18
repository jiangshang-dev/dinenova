package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.oss.FileStorageService;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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

    private FileStorageService fileStorageService;

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
        String fileType = file.getContentType();
        if (fileType == null || fileType.indexOf("image") == -1) {
            return getFailureResult(201, "上传的图片格式有误");
        }

        String original = file.getOriginalFilename().toLowerCase();
        if (original.indexOf("jpg") == -1 && original.indexOf("jpeg") == -1 && original.indexOf("png") == -1 && original.indexOf("gif") == -1 && original.indexOf("bmp") == -1) {
            return getFailureResult(201, "上传的图片格式有误");
        }

        try {
            String fileName = fileStorageService.upload(file);
            String url = fileStorageService.fileUrl(fileName);
            resultMap.put("status", "success");
            resultMap.put("domain", fileStorageService.getDomain());
            resultMap.put("filePath", url);
            resultMap.put("fileName", fileName);
            resultMap.put("state", "SUCCESS");
            resultMap.put("original", file.getOriginalFilename());
            resultMap.put("size", file.getSize() + "");
            resultMap.put("title", fileName);
            resultMap.put("type", file.getContentType());
            resultMap.put("url", url);
        } catch (Exception e) {
            logger.error("上传失败", e);
            return getFailureResult(201, "上传失败，请检查上传配置及权限");
        }

        return getSuccessResult(resultMap);
    }
}
