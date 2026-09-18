package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.CouponDto;
import com.dine.dto.UserInfo;
import com.dine.enums.CouponExpireTypeEnum;
import com.dine.param.CouponInfoParam;
import com.dine.param.CouponListParam;
import com.dine.param.CouponReceiveParam;
import com.dine.service.*;
import com.dine.util.DateUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.mapper.MtUserCouponMapper;
import com.dine.repository.model.MtCoupon;
import com.dine.repository.model.MtUserCoupon;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 卡券接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-卡券相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/coupon")
public class ClientCouponController extends BaseController {

    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 会员卡券服务接口
     * */
    private UserCouponService userCouponService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 获取卡券列表数据
     */
    @Operation(summary = "获取卡券列表数据")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody CouponListParam params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null != mtUser) {
            params.setUserId(mtUser.getId());
        }

        Map<String, Object> outParams = new HashMap();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        params.setMerchantId(merchantId);
        ResponseObject couponData = couponService.findCouponList(params);
        outParams.put("coupon", couponData.getData());

        ResponseObject responseObject = getSuccessResult(outParams);
        return getSuccessResult(responseObject.getData());
    }

    /**
     * 领取卡券
     * */
    @Operation(summary = "领取卡券")
    @Debounce
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject receive(HttpServletRequest request, @RequestBody CouponReceiveParam couponReceiveParam) {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (null != mtUser) {
            couponReceiveParam.setUserId(mtUser.getId());
        } else {
            return getFailureResult(1001);
        }

        try {
            userCouponService.receiveCoupon(couponReceiveParam);
        } catch (BusinessCheckException e) {
            return getFailureResult(1006, e.getMessage());
        }

        // 组织返回参数
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);

        return getSuccessResult(result);
    }

    /**
     * 查询卡券详情
     *
     * @param params Request对象
     */
    @Operation(summary = "查询卡券详情")
    @Debounce
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody CouponInfoParam params) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Integer couponId = params.getCouponId() == null ? 0 : params.getCouponId();
        String userCouponCode = params.getUserCouponCode() == null ? "" : params.getUserCouponCode();

        MtCoupon couponInfo = new MtCoupon();
        if (StringUtil.isNotEmpty(userCouponCode)) {
            MtUserCoupon userCouponInfo = mtUserCouponMapper.findByCode(userCouponCode);
            if (userCouponInfo != null) {
                couponInfo = couponService.queryCouponById(userCouponInfo.getCouponId());
            }
        } else {
            couponInfo = couponService.queryCouponById(couponId);
        }

        if (couponInfo == null) {
            return getFailureResult(201);
        }

        CouponDto couponDto = new CouponDto();
        BeanUtils.copyProperties(couponInfo, couponDto);
        couponDto.setIsReceive(false);

        // 是否需要领取码
        if (couponInfo.getReceiveCode() != null && StringUtil.isNotEmpty(couponInfo.getReceiveCode())) {
            couponDto.setNeedReceiveCode(true);
        } else {
            couponDto.setNeedReceiveCode(false);
        }

        if (null != mtUser) {
            List<MtUserCoupon> userCoupon = userCouponService.getUserCouponDetail(mtUser.getId(), couponId);
            if (userCoupon.size() >= couponInfo.getLimitNum() && couponInfo.getLimitNum() > 0) {
                couponDto.setIsReceive(true);
                couponDto.setUserCouponId(userCoupon.get(0).getId());
            }
        }

        // 适用店铺
        String storeNames = storeService.getStoreNames(couponInfo.getStoreIds());
        couponDto.setStoreNames(storeNames);

        String baseImage = settingService.getUploadBasePath();
        couponDto.setImage(baseImage + couponInfo.getImage());
        String effectiveDate = "";
        if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FIX.getKey())) {
            effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd HH:mm") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd");
        } else {
            effectiveDate = DateUtil.formatDate(couponInfo.getCreateTime(), "yyyy.MM.dd HH:mm") + " - 永久";
        }
        couponDto.setEffectiveDate(effectiveDate);
        couponDto.setGotNum(0);
        couponDto.setLimitNum(0);

        return getSuccessResult(couponDto);
    }
}
