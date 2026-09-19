package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dine.config.WebsiteProperties;
import com.dine.dto.AccountInfo;
import com.dine.enums.QrCodeEnum;
import com.dine.oss.FileStorageService;
import com.dine.service.CouponService;
import com.dine.service.QrBackgroundService;
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
import com.dine.repository.model.MtQrBackground;
import com.dine.repository.model.MtStore;
import com.dine.repository.model.MtTable;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private static final Pattern DIGIT = Pattern.compile("\\d+");

    private WebsiteProperties websiteProperties;

    private WeixinService weixinService;

    private StoreService storeService;

    private CouponService couponService;

    private TableService tableService;

    private FileStorageService fileStorageService;

    private QrBackgroundService qrBackgroundService;

    @Operation(summary = "生成二维码")
    @RequestMapping(value = "/createQrCode", method = RequestMethod.POST)
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
            String h5Page = websiteProperties.getUrl() + "#" + page;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(out, h5Page, 800, 800, "png", "");
            h5QrCode = new String(Base64Util.baseEncode(out.toByteArray()), "UTF-8");
            h5QrCode = "data:image/jpg;base64," + h5QrCode;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        String minAppQrCode = fileStorageService.fileUrl(weixinService.createQrCode(merchantId, type, id, page, 320));
        Map<String, Object> result = new HashMap<>();
        result.put("minAppQrCode", minAppQrCode);
        result.put("h5QrCode", h5QrCode);
        return getSuccessResult(result);
    }

    @Operation(summary = "生成带背景的二维码")
    @RequestMapping(value = "/createCode", method = RequestMethod.POST)
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

        String qrCode = "";
        if (appType == 1) {
            qrCode = websiteProperties.getUrl() + "wechat/" + page;
        } else if (appType == 2) {
            qrCode = websiteProperties.getUrl() + "#/" + page;
        }

        Map<String, String> generated = new LinkedHashMap<>();
        Map<String, String> styleNames = new HashMap<>();
        Integer storeId = resolveStoreId(type, id);
        try {
            for (String stored : systemBackgrounds()) {
                String fileName = stored.substring(stored.lastIndexOf('/') + 1);
                Matcher matcher = DIGIT.matcher(fileName);
                if (!matcher.find()) {
                    continue;
                }
                String numberStr = matcher.group();
                File output = composeQr(qrCode, width, name, stored, "qr-" + type + "-" + id + "-" + numberStr);
                if (output != null) {
                    generated.put(numberStr, output.getAbsolutePath());
                    styleNames.put(numberStr, "系统背景");
                }
            }
            if (storeId != null && storeId > 0) {
                List<MtQrBackground> mtQrBackgrounds = qrBackgroundService.listByStore(storeId);
                for (MtQrBackground background : mtQrBackgrounds) {
                    String key = "c" + background.getId();
                    File output = composeQr(qrCode, width, name, background.getFilePath(), "qr-" + type + "-" + id + "-" + key);
                    if (output != null) {
                        generated.put(key, output.getAbsolutePath());
                        styleNames.put(key, background.getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("生成二维码失败", e);
        }

        List<Map<String, Object>> styles = new ArrayList<>();
        for (Map.Entry<String, String> entry : generated.entrySet()) {
            File output = new File(entry.getValue());
            String url = "";
            try {
                url = fileStorageService.fileUrl(fileStorageService.upload(output));
            } catch (Exception e) {
                logger.error("上传二维码失败", e);
            } finally {
                FileUtil.del(output);
            }
            boolean custom = entry.getKey().startsWith("c");
            Map<String, Object> item = new HashMap<>();
            item.put("key", entry.getKey());
            item.put("url", url);
            item.put("custom", custom);
            item.put("name", styleNames.get(entry.getKey()));
            item.put("backgroundId", custom ? Integer.parseInt(entry.getKey().substring(1)) : 0);
            styles.add(item);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("storeId", storeId);
        data.put("styles", styles);
        return getSuccessResult(data);
    }

    @Operation(summary = "上传二维码背景")
    @Debounce
    @RequestMapping(value = "/uploadQrBackground", method = RequestMethod.POST)
    public ResponseObject uploadQrBackground(HttpServletRequest request, @RequestParam("file") MultipartFile file,
                                             @RequestParam("type") String type, @RequestParam("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = currentAccount(request);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Integer storeId = resolveStoreId(type, id);
        if (storeId == null || storeId < 1) {
            return getFailureResult(201, "请先选择店铺后再上传背景");
        }
        MtStore store = storeService.queryStoreById(storeId);
        if (store == null) {
            return getFailureResult(201, "店铺不存在");
        }
        String denied = denyStore(accountInfo, store);
        if (denied != null) {
            return getFailureResult(201, denied);
        }
        if (file == null || file.isEmpty()) {
            return getFailureResult(201, "请选择图片");
        }
        if (file.getSize() > 2L * 1024 * 1024) {
            return getFailureResult(201, "图片不能超过2MB");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String lower = original.toLowerCase();
        if (!(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg"))) {
            return getFailureResult(201, "只支持 jpg、png 图片");
        }
        if (qrBackgroundService.countActive(storeId) >= 8) {
            return getFailureResult(201, "每个店铺最多保存8张背景");
        }
        String stored;
        try {
            stored = fileStorageService.upload(file);
            File check = fileStorageService.materialize(stored);
            if (check == null || ImageIO.read(check) == null) {
                FileUtil.del(check);
                return getFailureResult(201, "图片无法识别，请换一张");
            }
            FileUtil.del(check);
        } catch (Exception e) {
            logger.error("保存二维码背景失败", e);
            return getFailureResult(201, "上传失败，请检查对象存储配置及权限");
        }
        String name = original;
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        if (name.length() > 40) {
            name = name.substring(name.length() - 40);
        }
        MtQrBackground background = new MtQrBackground();
        background.setMerchantId(store.getMerchantId() == null ? 0 : store.getMerchantId());
        background.setStoreId(storeId);
        background.setName(name);
        background.setFilePath(stored);
        background.setStatus("A");
        background.setCreateTime(new Date());
        qrBackgroundService.save(background);
        return getSuccessResult(background.getId());
    }

    @Operation(summary = "删除二维码背景")
    @Debounce
    @RequestMapping(value = "/deleteQrBackground", method = RequestMethod.POST)
    public ResponseObject deleteQrBackground(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        AccountInfo accountInfo = currentAccount(request);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        Integer backgroundId = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());
        MtQrBackground background = qrBackgroundService.getById(backgroundId);
        if (background == null || !"A".equals(background.getStatus())) {
            return getFailureResult(201, "背景不存在");
        }
        MtStore store = storeService.queryStoreById(background.getStoreId());
        if (store == null) {
            return getFailureResult(201, "店铺不存在");
        }
        String denied = denyStore(accountInfo, store);
        if (denied != null) {
            return getFailureResult(201, denied);
        }
        background.setStatus("D");
        qrBackgroundService.updateById(background);
        return getSuccessResult(true);
    }

    @Operation(summary = "批量生成桌码")
    @Debounce
    @RequestMapping(value = "/batchCreateTableCode", method = RequestMethod.POST)
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

        String bgStored = pickBackground(styleId);
        File bgFile = fileStorageService.materialize(bgStored);
        if (bgFile == null) {
            return getFailureResult(201, "没有可用的二维码背景");
        }

        String formattedTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
        File outDir = new File(System.getProperty("java.io.tmpdir"), "dinenova-table-" + formattedTime);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        LambdaQueryWrapper<MtTable> tableLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tableLambdaQueryWrapper.eq(mtTable.getStoreId() != null, MtTable::getStoreId, mtTable.getStoreId());
        List<MtTable> mtTableList = tableService.list(tableLambdaQueryWrapper);
        for (MtTable mtTableI : mtTableList) {
            String page = QrCodeEnum.TABLE.getPage() + "?" + QrCodeEnum.TABLE.getKey() + "Id=" + mtTableI.getId();
            String qrCode = appType == 1 ? websiteProperties.getUrl() + "wechat/" + page : websiteProperties.getUrl() + "#/" + page;
            File outputFile = new File(outDir, mtTableI.getCode() + ".png");
            String fileName = showName == 1 ? mtTableI.getCode() : "";
            QRCodeUtil.createCode(qrCode, width, fileName, bgFile.getAbsolutePath(), outputFile.getAbsolutePath());
        }
        FileUtil.del(bgFile);

        try {
            Path sourcePath = outDir.toPath();
            Path zipFilePath = sourcePath.getParent().resolve(outDir.getName() + ".zip");
            FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            zipFolderContents(sourcePath.toFile(), sourcePath, zipOut);
            zipOut.close();
            fos.close();

            String upload = fileStorageService.upload(zipFilePath.toFile());
            FileUtil.del(zipFilePath);
            FileUtil.del(outDir);
            return getSuccessResult(fileStorageService.fileUrl(upload));
        } catch (IOException e) {
            logger.error("打包桌码失败", e);
        }
        return getSuccessResult(null);
    }

    private AccountInfo currentAccount(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        return TokenUtil.getAccountInfoByToken(token);
    }

    private Integer resolveStoreId(String type, Integer id) throws BusinessCheckException {
        if (id == null || id < 1 || type == null) {
            return null;
        }
        if (type.equals(QrCodeEnum.STORE.getKey())) {
            return id;
        }
        if (type.equals(QrCodeEnum.TABLE.getKey())) {
            MtTable table = tableService.queryTableById(id);
            return table == null ? null : table.getStoreId();
        }
        return null;
    }

    private String denyStore(AccountInfo accountInfo, MtStore store) {
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0
                && !accountInfo.getStoreId().equals(store.getId())) {
            return "只能管理本店的二维码背景";
        }
        if ((accountInfo.getStoreId() == null || accountInfo.getStoreId() < 1)
                && accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0
                && store.getMerchantId() != null
                && !accountInfo.getMerchantId().equals(store.getMerchantId())) {
            return "只能管理本商户的二维码背景";
        }
        return null;
    }

    private List<String> systemBackgrounds() throws IOException {
        List<String> stored = fileStorageService.list("qrcode");
        List<String> images = new ArrayList<>();
        for (String path : stored) {
            String lower = path.toLowerCase();
            if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                images.add(path);
            }
        }
        if (!images.isEmpty()) {
            return images;
        }
        File white = tempPng("qr-default");
        BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 800, 800);
        g.dispose();
        ImageIO.write(img, "png", white);
        String path = fileStorageService.upload(white, "qrcode/1.png");
        FileUtil.del(white);
        images.add(path);
        return images;
    }

    private String pickBackground(String styleId) {
        try {
            List<String> images = systemBackgrounds();
            for (String path : images) {
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                Matcher matcher = DIGIT.matcher(fileName);
                if (matcher.find() && matcher.group().equals(styleId)) {
                    return path;
                }
            }
            return images.isEmpty() ? null : images.get(0);
        } catch (IOException e) {
            logger.error("读取二维码背景失败", e);
            return null;
        }
    }

    private File composeQr(String qrCode, Integer width, String name, String storedPath, String tempName) throws IOException {
        File bg = fileStorageService.materialize(storedPath);
        if (bg == null || !bg.isFile()) {
            return null;
        }
        File output = tempPng(tempName);
        QRCodeUtil.createCode(qrCode, width, name, bg.getAbsolutePath(), output.getAbsolutePath());
        FileUtil.del(bg);
        return output.exists() ? output : null;
    }

    private File tempPng(String name) throws IOException {
        File dir = new File(System.getProperty("java.io.tmpdir"), "dinenova-work");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, name + "-" + UUID.randomUUID().toString().replace("-", "") + ".png");
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
