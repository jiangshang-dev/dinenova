package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.enums.PositionEnum;
import com.dine.enums.StatusEnum;
import com.dine.service.BannerService;
import com.dine.service.MerchantService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtBanner;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-页面相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/page")
public class ClientPageController extends BaseController {

    /**
     * 焦点图服务接口
     * */
    private BannerService bannerService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 获取页面数据
     */
    @Operation(summary = "获取首页页面数据")
    @Debounce
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject getPageData(HttpServletRequest request, @RequestParam Map<String, Object> param) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (storeId > 0) {
            params.put("storeId", storeId);
        }
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        if (merchantId > 0) {
            params.put("merchantId", merchantId);
        }
        params.put("position", PositionEnum.M_HOME_BANNER.getKey());
        List<MtBanner> bannerData = bannerService.queryBannerListByParams(params);
        params.put("position", PositionEnum.M_HOME_ADS.getKey());
        List<MtBanner> adsData = bannerService.queryBannerListByParams(params);

        Map<String, Object> outParams = new HashMap();
        outParams.put("banner", bannerData);
        outParams.put("ads", adsData);
        return getSuccessResult(outParams);
    }
}
