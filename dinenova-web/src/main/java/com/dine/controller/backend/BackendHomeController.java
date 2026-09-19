package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.dto.UserOrderDto;
import com.dine.service.AccountService;
import com.dine.service.MemberService;
import com.dine.service.OrderService;
import com.dine.util.DateUtil;
import com.dine.util.TimeUtils;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.TAccount;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-首页相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/home")
public class BackendHomeController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 后台账号服务接口
     * */
    private AccountService accountService;

    /**
     * 首页统计数据
     *
     * @return
     */
    @Operation(summary = "首页统计数据")
    @Debounce
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ResponseObject index(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        Date beginTime = DateUtil.getDayBegin();
        Date endTime = DateUtil.getDayEnd();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId();

        // 总会员数
        Long totalUser = memberService.getUserCount(merchantId, storeId);
        // 今日新增会员数量
        Long todayUser = memberService.getUserCount(merchantId, storeId, beginTime, endTime);

        // 总订单数
        BigDecimal totalOrder = orderService.getOrderCount(merchantId, storeId);
        // 今日订单数
        BigDecimal todayOrder = orderService.getOrderCount(merchantId, storeId, beginTime, endTime);

        // 今日交易金额
        BigDecimal todayPay = orderService.getPayMoney(merchantId, storeId, beginTime, endTime);
        // 总交易金额
        BigDecimal totalPay = orderService.getPayMoney(merchantId, storeId);

        // 今日活跃会员数
        Long todayActiveUser = memberService.getActiveUserCount(merchantId, storeId, beginTime, endTime);

        // 总支付人数
        Integer totalPayUser = orderService.getPayUserCount(merchantId, storeId);

        Map<String, Object> result = new HashMap<>();

        result.put("todayUser", todayUser);
        result.put("totalUser", totalUser);
        result.put("todayOrder", todayOrder);
        result.put("totalOrder", totalOrder);
        result.put("todayPay", todayPay);
        result.put("totalPay", totalPay);
        result.put("todayActiveUser", todayActiveUser);
        result.put("totalPayUser", totalPayUser);

        return getSuccessResult(result);
    }

    /**
     * 首页图表统计数据
     *
     * @param request
     * @return
     */
    @Operation(summary = "首页图表统计数据")
    @Debounce
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    public ResponseObject statistic(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String tag = request.getParameter("tag") == null ? "order,user_active" : request.getParameter("tag");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer merchantId = account.getMerchantId() == null ? 0 : account.getMerchantId();
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();

        ArrayList<String> days = TimeUtils.getDays(5);
        days.add("昨天");
        days.add("今天");

        Map<String, Object> result = new HashMap<>();
        if (tag.equals("payment")) {
            BigDecimal[] orderPayData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            for (int i = 0; i < 7; i++) {
                Date beginTime = DateUtil.getDayBegin((6 - i));
                Date endTime = DateUtil.getDayEnd((6 - i));
                BigDecimal payMoney = orderService.getPayMoney(merchantId, storeId, beginTime, endTime);
                orderPayData[i] = payMoney == null ? new BigDecimal("0") : payMoney;
            }
            BigDecimal data[][] = { orderPayData };
            result.put("data", data);
        } else {
            BigDecimal[] orderCountData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};
            BigDecimal[] userCountData = {new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")};

            for (int i = 0; i < 7; i++) {
                Date beginTime = DateUtil.getDayBegin((6 - i));
                Date endTime = DateUtil.getDayEnd((6 - i));
                orderCountData[i] = orderService.getOrderCount(merchantId, storeId, beginTime, endTime);
                Long userCount = memberService.getActiveUserCount(merchantId, storeId, beginTime, endTime);
                userCountData[i] = new BigDecimal(userCount);
            }
            BigDecimal data[][] = { orderCountData, userCountData };
            result.put("data", data);
        }

        result.put("labels", days);

        return getSuccessResult(result);
    }

    /**
     * 获取收款结果
     *
     * @param request
     * @return
     */
    @Operation(summary = "获取收款结果")
    @Debounce
    @RequestMapping(value = "/cashierResult", method = RequestMethod.GET)
    public ResponseObject cashierResult(HttpServletRequest request) throws BusinessCheckException {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));

        UserOrderDto orderInfo = orderService.getOrderById(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }
}
