package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.dto.ParamDto;
import com.dine.dto.UserCouponDto;
import com.dine.enums.CouponExpireTypeEnum;
import com.dine.enums.CouponTypeEnum;
import com.dine.service.AccountService;
import com.dine.service.ConfirmLogService;
import com.dine.service.CouponService;
import com.dine.service.MemberService;
import com.dine.util.DateUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.mapper.MtUserCouponMapper;
import com.dine.repository.model.MtCoupon;
import com.dine.repository.model.MtUser;
import com.dine.repository.model.MtUserCoupon;
import com.dine.repository.model.TAccount;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡券核销类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-卡券核销相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/doConfirm")
public class BackendDoConfirmController extends BaseController {

    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 账户服务接口
     */
    private AccountService accountService;

    /**
     * 核销记录服务接口
     * */
    private ConfirmLogService confirmLogService;

    /**
     * 核销详情
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @Operation(summary = "核销详情")
    @Debounce
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('coupon:confirm:index')")
    public ResponseObject info(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String userCouponId = param.get("id") == null ? "" : param.get("id").toString();
        String userCouponCode = param.get("code") == null ? "" : param.get("code").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (StringUtil.isEmpty(userCouponCode) && StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "核销券码不能为空");
        }

        // 通过券码或ID获取
        MtUserCoupon userCoupon;
        if (!StringUtil.isEmpty(userCouponCode)) {
            userCoupon = mtUserCouponMapper.findByCode(userCouponCode);
        } else {
            userCoupon = mtUserCouponMapper.selectById(Integer.parseInt(userCouponId));
        }

        if (userCoupon == null) {
            return getFailureResult(201, "未查询到该卡券信息");
        }

        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());
        MtUser userInfo = memberService.queryMemberById(userCoupon.getUserId());

        String effectiveDate = "";
        if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FIX.getKey())) {
            effectiveDate = DateUtil.formatDate(couponInfo.getBeginTime(), "yyyy.MM.dd HH:mm") + " - " + DateUtil.formatDate(couponInfo.getEndTime(), "yyyy.MM.dd HH:mm");
        }
        if (couponInfo.getExpireType().equals(CouponExpireTypeEnum.FLEX.getKey())) {
            effectiveDate = DateUtil.formatDate(userCoupon.getCreateTime(), "yyyy.MM.dd HH:mm") + " - " + DateUtil.formatDate(userCoupon.getExpireTime(), "yyyy.MM.dd HH:mm");
        }

        UserCouponDto userCouponInfo = new UserCouponDto();
        userCouponInfo.setName(couponInfo.getName());
        userCouponInfo.setEffectiveDate(effectiveDate);
        userCouponInfo.setDescription(couponInfo.getDescription());
        userCouponInfo.setId(userCoupon.getId());
        userCouponInfo.setType(couponInfo.getType());
        userCouponInfo.setStatus(userCoupon.getStatus());
        userCouponInfo.setBalance(userCoupon.getBalance());
        userCouponInfo.setAmount(userCoupon.getAmount());
        userCouponInfo.setCode(userCoupon.getCode());
        userCouponInfo.setUseRule(couponInfo.getOutRule());
        Long confirmCount = confirmLogService.getConfirmNum(userCoupon.getId());
        userCouponInfo.setConfirmCount(confirmCount.intValue());

        // 卡券类型列表
        CouponTypeEnum[] typeListEnum = CouponTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (CouponTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("couponInfo", userCouponInfo);
        result.put("userInfo", userInfo);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 确认核销
     *
     * @param request  HttpServletRequest对象
     * @return
     */
    @Operation(summary = "确认核销")
    @Debounce
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('coupon:confirm:index')")
    public ResponseObject doConfirm(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        String userCouponId = param.get("userCouponId") == null ? "" : param.get("userCouponId").toString();
        String amount = (param.get("amount") == null || StringUtil.isEmpty(param.get("amount").toString())) ? "0" : param.get("amount").toString();
        String remark = (param.get("remark") == null || StringUtil.isEmpty(param.get("remark").toString())) ? "后台核销" : param.get("remark").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (StringUtil.isEmpty(userCouponId)) {
            return getFailureResult(201, "系统参数有误");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();

        MtUserCoupon mtUserCoupon = mtUserCouponMapper.selectById(Integer.parseInt(userCouponId));
        if (mtUserCoupon.getType().equals(CouponTypeEnum.PRESTORE.getKey()) && StringUtil.isEmpty(amount)) {
            return getFailureResult(201, "储值卡核销金额不能为空");
        }

        try {
            couponService.useCoupon(Integer.parseInt(userCouponId), accountInfo.getId(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(201, "核销失败：" + e.getMessage());
        }

        return getSuccessResult(true);
    }
}
