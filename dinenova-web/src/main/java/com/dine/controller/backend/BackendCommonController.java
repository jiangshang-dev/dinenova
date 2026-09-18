package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dine.dto.AccountInfo;
import com.dine.enums.QrCodeEnum;
import com.dine.oss.FileStorageService;
import com.dine.service.CouponService;
import com.dine.service.SettingService;
import com.dine.service.StoreService;
import com.dine.service.TableService;
import com.dine.service.WeixinService;
import com.dine.util.Base64Util;
import com.dine.util.QRCodeUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtCoupon;
import com.dine.repository.model.MtStore;
import com.dine.repository.model.MtTable;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 后台公共接口控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-公共接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/common")
public class BackendCommonController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BackendCommonController.class);

    private Environment env;

    /**
     * 微信服务接口
     * */
    private WeixinService weixinService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 桌码服务接口
     */
    private TableService tableService;

    /**
     * 文件存储
     */
    private FileStorageService fileStorageService;

    /**
     * 生成二维码
     *
     * @return
     */
    @Operation(summary = "生成二维码")
    @Debounce
    @RequestMapping(value = "/createQrCode", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject createQrCode(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String type = params.get("type") != null ? params.get("type").toString() : "";
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Integer merchantId = 0;
        String page = QrCodeEnum.STORE.getPage() + "?" + QrCodeEnum.STORE.getKey() + "Id=" + id;
        if (type.equals(QrCodeEnum.TABLE.getKey())) {
            page = QrCodeEnum.TABLE.getPage() + "?" + QrCodeEnum.TABLE.getKey() + "Id=" + id;
        }
        if (type.equals(QrCodeEnum.COUPON.getKey())) {
            page = QrCodeEnum.COUPON.getPage() + "?" + QrCodeEnum.COUPON.getKey() + "Id=" + id;
        }
        if (type.equals(QrCodeEnum.STORE.getKey())) {
            MtStore mtStore = storeService.queryStoreById(id);
            if (mtStore != null) {
                merchantId = mtStore.getMerchantId();
            }
        }
        if (type.equals(QrCodeEnum.COUPON.getKey())) {
            MtCoupon mtCoupon = couponService.queryCouponById(id);
            if (mtCoupon != null) {
                merchantId = mtCoupon.getMerchantId();
            }
        }
        String h5QrCode = "";
        try {
            String h5Page = env.getProperty("website.url") + "#" + page;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(out, h5Page, 800, 800, "png", "");
            h5QrCode = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");
            h5QrCode = "data:image/jpg;base64," + h5QrCode;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        String imagePath = settingService.getUploadBasePath();
        String minAppQrCode = weixinService.createQrCode(merchantId, type, id, page, 320);
        minAppQrCode = imagePath + minAppQrCode;

        Map<String, Object> result = new HashMap<>();
        result.put("minAppQrCode", minAppQrCode);
        result.put("h5QrCode", h5QrCode);

        return getSuccessResult(result);
    }

    /**
     * 生成带背景样式的二维码。appType：1 小程序，2 h5。
     */
    @Operation(summary = "生成带背景的二维码")
    @Debounce
    @RequestMapping(value = "/createCode", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject createCode(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String type = params.get("type") != null ? params.get("type").toString() : "";
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String page = QrCodeEnum.STORE.getPage() + "?" + QrCodeEnum.STORE.getKey() + "Id=" + id;
        if (type.equals(QrCodeEnum.TABLE.getKey())) {
            page = QrCodeEnum.TABLE.getPage() + "?" + QrCodeEnum.TABLE.getKey() + "Id=" + id;
        }
        if (type.equals(QrCodeEnum.COUPON.getKey())) {
            page = QrCodeEnum.COUPON.getPage() + "?" + QrCodeEnum.COUPON.getKey() + "Id=" + id;
        }

        Integer width = params.get("width") == null ? 400 : Integer.parseInt(params.get("width").toString());
        if (width <= 0) {
            width = 400;
        }
        Integer showName = params.get("showName") == null ? 0 : Integer.parseInt(params.get("showName").toString());
        Integer appType = params.get("appType") == null ? 1 : Integer.parseInt(params.get("appType").toString());

        String name = "";
        if (showName == 1) {
            if (type.equals(QrCodeEnum.STORE.getKey())) {
                MtStore mtStore = storeService.queryStoreById(id);
                if (mtStore != null) {
                    name = mtStore.getName();
                }
            } else if (type.equals(QrCodeEnum.TABLE.getKey())) {
                MtTable mtTable = tableService.queryTableById(id);
                if (mtTable != null) {
                    name = mtTable.getCode();
                }
            }
        }

        String website = env.getProperty("website.url");
        String qrCode = "";
        if (appType == 1) {
            qrCode = website + "wechat/" + page;
        } else if (appType == 2) {
            qrCode = website + "#/" + page;
        }

        String pathRoot = env.getProperty("images.root");
        String baseImage = env.getProperty("images.path");
        Map<String, String> result = new HashMap<>();
        try {
            File directory = ensureQrBackground(pathRoot, baseImage);
            File[] files = directory.listFiles();
            if (files != null) {
                Pattern pattern = Pattern.compile("\\d+");
                for (File file : files) {
                    if (!file.isFile()) {
                        continue;
                    }
                    Matcher matcher = pattern.matcher(file.getName());
                    if (!matcher.find()) {
                        continue;
                    }
                    String numberStr = matcher.group();
                    String codeName = "Qr-" + type + "-" + appType + "-" + id + "-" + numberStr + ".png";
                    String bgName = file.getAbsolutePath();
                    String outputFile = pathRoot + baseImage + codeName;
                    QRCodeUtil.createCode(qrCode, width, name, bgName, outputFile, pathRoot, baseImage);
                    result.put(numberStr, codeName);
                }
            }
        } catch (Exception e) {
            logger.error("生成二维码失败", e);
        }

        for (Map.Entry<String, String> entry : result.entrySet()) {
            String filePath = entry.getValue();
            String path = pathRoot + baseImage + filePath;
            if (fileStorageService.isRemote()) {
                String upload = fileStorageService.upload(new File(path));
                result.put(entry.getKey(), fileStorageService.getDomain() + upload);
                FileUtil.del(path);
            } else {
                result.put(entry.getKey(), publicFileUrl(baseImage + filePath));
            }
        }
        return getSuccessResult(result);
    }

    /**
     * 批量生成桌码并打包下载。
     */
    @Operation(summary = "批量生成桌码")
    @Debounce
    @RequestMapping(value = "/batchCreateTableCode", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject batchCreateTableCode(HttpServletResponse response, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());
        MtTable mtTable = tableService.queryTableById(id);
        if (mtTable == null) {
            return getFailureResult(201, "桌码不存在");
        }

        Integer width = params.get("width") == null ? 400 : Integer.parseInt(params.get("width").toString());
        if (width <= 0) {
            width = 400;
        }
        Integer showName = params.get("showName") == null ? 0 : Integer.parseInt(params.get("showName").toString());
        Integer appType = params.get("appType") == null ? 1 : Integer.parseInt(params.get("appType").toString());
        String styleId = params.get("styleId") != null ? params.get("styleId").toString() : "";

        String pathRoot = env.getProperty("images.root");
        String baseImage = env.getProperty("images.path");
        String website = env.getProperty("website.url");
        String bgInputStr = "";
        try {
            File directory = ensureQrBackground(pathRoot, baseImage);
            File[] files = directory.listFiles();
            Pattern pattern = Pattern.compile("\\d+");
            if (files != null) {
                for (File file : files) {
                    if (!file.isFile()) {
                        continue;
                    }
                    Matcher matcher = pattern.matcher(file.getName());
                    if (matcher.find() && matcher.group().equals(styleId)) {
                        bgInputStr = file.getAbsolutePath();
                        break;
                    }
                }
            }
            if (bgInputStr.isEmpty() && files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                        bgInputStr = file.getAbsolutePath();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("读取二维码背景失败", e);
        }

        String formattedTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
        File outDir = new File(pathRoot + baseImage + formattedTime);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        LambdaQueryWrapper<MtTable> tableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tableLambdaQueryWrapper.eq(mtTable.getStoreId() != null, MtTable::getStoreId, mtTable.getStoreId());
        List<MtTable> mtTableList = tableService.list(tableLambdaQueryWrapper);
        for (MtTable mtTableI : mtTableList) {
            String page = QrCodeEnum.TABLE.getPage() + "?" + QrCodeEnum.TABLE.getKey() + "Id=" + mtTableI.getId();
            String qrCode = appType == 1 ? website + "wechat/" + page : website + "#/" + page;
            String codeName = formattedTime + "/" + mtTableI.getCode() + ".png";
            String outputFile = pathRoot + baseImage + codeName;
            String fileName = showName == 1 ? mtTableI.getCode() : "";
            QRCodeUtil.createCode(qrCode, width, fileName, bgInputStr, outputFile, pathRoot, baseImage);
        }

        String outputFile = pathRoot + baseImage + formattedTime;
        try {
            Path sourcePath = Paths.get(outputFile);
            Path parentPath = sourcePath.getParent();
            String zipFileName = sourcePath.getFileName().toString() + ".zip";
            Path zipFilePath = parentPath.resolve(zipFileName);
            FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            zipFolderContents(sourcePath.toFile(), sourcePath, zipOut);
            zipOut.close();
            fos.close();

            if (fileStorageService.isRemote()) {
                String upload = fileStorageService.upload(zipFilePath.toFile());
                FileUtil.del(zipFilePath);
                FileUtil.del(outputFile);
                return getSuccessResult(fileStorageService.getDomain() + upload);
            }
            return getSuccessResult(publicFileUrl(baseImage + formattedTime + ".zip"));
        } catch (IOException e) {
            logger.error("打包桌码失败", e);
        }
        return getSuccessResult(null);
    }

    private File ensureQrBackground(String pathRoot, String baseImage) throws IOException {
        File directory = new File(pathRoot + baseImage + "qrcode/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File[] files = directory.listFiles();
        boolean hasImage = false;
        if (files != null) {
            for (File file : files) {
                String lower = file.getName().toLowerCase();
                if (file.isFile() && (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) {
                    hasImage = true;
                    break;
                }
            }
        }
        if (!hasImage) {
            BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 800, 800);
            g.dispose();
            ImageIO.write(img, "png", new File(directory, "1.png"));
        }
        return directory;
    }

    private String publicFileUrl(String path) {
        String uploadUrl = env.getProperty("images.upload.url", "");
        if (uploadUrl.endsWith("/")) {
            uploadUrl = uploadUrl.substring(0, uploadUrl.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return uploadUrl + path;
    }

    private void zipFolderContents(File folder, Path sourcePath, ZipOutputStream zipOut) throws IOException {
        File[] children = folder.listFiles();
        if (children == null) {
            return;
        }
        for (File file : children) {
            if (file.isDirectory()) {
                zipFolderContents(file, sourcePath, zipOut);
            } else {
                String relativePath = sourcePath.relativize(file.toPath()).toString();
                zipOut.putNextEntry(new ZipEntry(relativePath));
                Files.copy(file.toPath(), zipOut);
                zipOut.closeEntry();
            }
        }
    }
}
