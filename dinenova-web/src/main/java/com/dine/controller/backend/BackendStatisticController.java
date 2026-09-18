package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.dto.GoodsTopDto;
import com.dine.dto.MemberTopDto;
import com.dine.service.GoodsService;
import com.dine.service.MemberService;
import com.dine.service.OrderService;
import com.dine.util.DateUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 数据统计控制器
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-数据统计相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/statistic")
public class BackendStatisticController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 商品服务接口
     * */
    private GoodsService goodsService;

    /**
     * 数据概况
     *
     * @return
     */
    @Operation(summary = "数据概况")
    @Debounce
    @RequestMapping(value = "/main", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject main(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException, ParseException {
        String token = request.getHeader("Access-Token");
        String startTimeStr = param.get("startTime") == null ? "" : param.get("startTime").toString();
        String endTimeStr = param.get("endTime") == null ? "" : param.get("endTime").toString();

        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId();

        // 总会员数
        Long totalUserCount = memberService.getUserCount(merchantId, storeId);
        // 新增会员数量
        Long userCount = memberService.getUserCount(merchantId, storeId, startTime, endTime);

        // 总订单数
        BigDecimal totalOrderCount = orderService.getOrderCount(merchantId, storeId);
        // 订单数
        BigDecimal orderCount = orderService.getOrderCount(merchantId, storeId, startTime, endTime);

        // 交易金额
        BigDecimal payAmount = orderService.getPayMoney(merchantId, storeId, startTime, endTime);
        // 总交易金额
        BigDecimal totalPayAmount = orderService.getPayMoney(merchantId, storeId);

        // 活跃会员数
        Long activeUserCount = memberService.getActiveUserCount(merchantId, storeId, startTime, endTime);

        // 总支付人数
        Integer totalPayUserCount = orderService.getPayUserCount(merchantId, storeId);

        Map<String, Object> result = new HashMap<>();

        result.put("userCount", userCount);
        result.put("totalUserCount", totalUserCount);
        result.put("orderCount", orderCount);
        result.put("totalOrderCount", totalOrderCount);
        result.put("payAmount", payAmount);
        result.put("totalPayAmount", totalPayAmount);
        result.put("activeUserCount", activeUserCount);
        result.put("totalPayUserCount", totalPayUserCount);

        return getSuccessResult(result);
    }

    /**
     * 排行榜数据
     *
     * @return
     */
    @Operation(summary = "排行榜数据")
    @Debounce
    @RequestMapping(value = "/top", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject top(HttpServletRequest request, @RequestBody Map<String, Object> param) throws ParseException {
        String token = request.getHeader("Access-Token");
        String startTimeStr = param.get("startTime") == null ? "" : param.get("startTime").toString();
        String endTimeStr = param.get("endTime") == null ? "" : param.get("endTime").toString();

        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId();

        Map<String, Object> result = new HashMap<>();

        List<GoodsTopDto> goodsList = goodsService.getGoodsSaleTopList(merchantId, storeId, startTime, endTime);
        List<MemberTopDto> memberList = memberService.getMemberConsumeTopList(merchantId, storeId, startTime, endTime);

        result.put("goodsList", goodsList);
        result.put("memberList", memberList);

        return getSuccessResult(result);
    }

    /**
     * 获取会员数量
     *
     * @return
     */
    @Operation(summary = "获取会员数量")
    @Debounce
    @RequestMapping(value = "/totalMember", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject totalMember(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer merchantId = accountInfo.getMerchantId();
        Integer storeId = accountInfo.getStoreId();

        Long totalMember = memberService.getUserCount(merchantId, storeId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalMember", totalMember);

        return getSuccessResult(result);
    }
}
