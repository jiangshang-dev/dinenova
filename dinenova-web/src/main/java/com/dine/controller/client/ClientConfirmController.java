package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.UserInfo;
import com.dine.enums.StatusEnum;
import com.dine.param.ConfirmParam;
import com.dine.service.CouponService;
import com.dine.service.MemberService;
import com.dine.service.StaffService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.mapper.MtUserCouponMapper;
import com.dine.repository.model.MtCoupon;
import com.dine.repository.model.MtStaff;
import com.dine.repository.model.MtUser;
import com.dine.repository.model.MtUserCoupon;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 卡券核销controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-卡券核销相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/confirm")
public class ClientConfirmController extends BaseController {

    private MtUserCouponMapper mtUserCouponMapper;

    /**
     * 卡券服务接口
     */
    private CouponService couponService;

    /**
     * 员工服务接口
     * */
    private StaffService staffService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 核销卡券
     *
     * @param confirmParam Request对象
     */
    @Operation(summary = "核销卡券")
    @Debounce
    @RequestMapping(value = "/doConfirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject doConfirm(HttpServletRequest request, @RequestBody ConfirmParam confirmParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String code = confirmParam.getCode() == null ? "" : confirmParam.getCode();
        String amount = (confirmParam.getAmount() == null || confirmParam.getAmount() == "") ? "0" : confirmParam.getAmount();
        String remark = confirmParam.getRemark() == null ? "" : confirmParam.getRemark();

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        if (loginInfo == null) {
            return getFailureResult(1001);
        }

        MtUserCoupon userCoupon = mtUserCouponMapper.findByCode(code);
        if (null == userCoupon) {
            return getFailureResult(1003, "该券不存在！");
        }

        // 券码已过期
        if (couponService.codeExpired(code)) {
            return getFailureResult(1003, "二维码已过期，请重新获取！");
        }
        MtUser mtUser = memberService.queryMemberById(loginInfo.getId());
        MtCoupon couponInfo = couponService.queryCouponById(userCoupon.getCouponId());

        // 员工是否已经被审核
        HashMap params = new HashMap<>();
        params.put("MOBILE", mtUser.getMobile());
        params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
        List<MtStaff> staffList = staffService.queryStaffByParams(params);
        Integer storeId = 0;
        if (staffList.size() > 0) {
            for (MtStaff staff : staffList) {
                if (staff.getStoreId() > 0) {
                    storeId = staff.getStoreId();
                }
                String storeIdsStr = couponInfo.getStoreIds();
                if (StringUtil.isNotEmpty(storeIdsStr)) {
                    String[] storeIds = couponInfo.getStoreIds().split(",");
                    Boolean isSameStore = false;
                    for (String hid : storeIds) {
                        if (staff.getStoreId().toString().equals(hid)) {
                            isSameStore = true;
                            break;
                        }
                    }
                    if (!isSameStore) {
                        return getFailureResult(1003, "员工对该卡券没有权限");
                    }
                }
            }
        } else {
            return getFailureResult(1003, "员工状态异常！");
        }

        Integer userCouponId = userCoupon.getId();
        String confirmCode = "";

        try {
            confirmCode = couponService.useCoupon(userCouponId, mtUser.getId(), storeId, 0, new BigDecimal(amount), remark);
        } catch (BusinessCheckException e) {
            return getFailureResult(1003, e.getMessage());
        }

        // 获取最新余额
        MtUserCoupon userCouponNew = mtUserCouponMapper.selectById(userCoupon.getId());

        // 组织返回参数
        Map<String, Object> result = new HashMap<>();
        result.put("result", true);
        result.put("money", couponInfo.getAmount());
        result.put("balance", userCouponNew.getBalance());
        result.put("tips", "");
        result.put("name", couponInfo.getName());
        result.put("code", confirmCode);
        result.put("status", userCouponNew.getStatus());

        return getSuccessResult(result);
    }
}
