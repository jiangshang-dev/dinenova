package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.oss.FileStorageService;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传管理控制类
 */
@Tag(name = "管理端-文件上传相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/file")
public class BackendFileController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendFileController.class);

    private FileStorageService fileStorageService;

    @Operation(summary = "后台上传文件")
    @Debounce
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
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
