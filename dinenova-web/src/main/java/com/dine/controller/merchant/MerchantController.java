package com.dine.controller.merchant;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.UserInfo;
import com.dine.service.ConfirmLogService;
import com.dine.service.MemberService;
import com.dine.service.OrderService;
import com.dine.service.StaffService;
import com.dine.util.DateUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtStaff;
import com.dine.repository.model.MtUser;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "商户端-商户信息相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/merchant")
public class MerchantController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 卡券核销记录服务接口
     * */
    private ConfirmLogService confirmLogService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 查询商户信息
     *
     * @param request Request对象
     */
    @Operation(summary = "查询商户信息")
    @Debounce
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject info(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (null == userInfo) {
            return getFailureResult(1001, "用户未登录");
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("userInfo", mtUser);

        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        if (null == staffInfo) {
            return getFailureResult(1002, "该账号不是商户");
        }

        outParams.put("confirmInfo", staffInfo);

        // 总收款额
        Date beginTime = DateUtil.getDayBegin();
        Date endTime = DateUtil.getDayEnd();
        BigDecimal payMoney = orderService.getPayMoney(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("payMoney", payMoney);

        // 总会员数
        Long userCount = memberService.getUserCount(staffInfo.getMerchantId(), staffInfo.getStoreId());
        outParams.put("userCount", userCount);

        // 今日订单数
        BigDecimal orderCount = orderService.getOrderCount(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("orderCount", orderCount);

        // 核销券数
        Long confirmCount = confirmLogService.getConfirmCount(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("couponCount", confirmCount);

        // 今日活跃会员数
        Long todayUser = memberService.getActiveUserCount(staffInfo.getMerchantId(), staffInfo.getStoreId(), beginTime, endTime);
        outParams.put("todayUser", todayUser);

        return getSuccessResult(outParams);
    }
}
