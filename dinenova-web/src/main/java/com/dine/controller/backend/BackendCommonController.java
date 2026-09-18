package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.enums.QrCodeEnum;
import com.dine.service.CouponService;
import com.dine.service.SettingService;
import com.dine.service.StoreService;
import com.dine.service.WeixinService;
import com.dine.util.Base64Util;
import com.dine.util.QRCodeUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtCoupon;
import com.dine.repository.model.MtStore;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

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
}
