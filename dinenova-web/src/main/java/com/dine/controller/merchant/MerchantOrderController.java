package com.dine.controller.merchant;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.UserInfo;
import com.dine.dto.UserOrderDto;
import com.dine.param.OrderDetailParam;
import com.dine.param.OrderListParam;
import com.dine.service.MemberService;
import com.dine.service.OrderService;
import com.dine.service.StaffService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtOrder;
import com.dine.repository.model.MtStaff;
import com.dine.repository.model.MtUser;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 订单类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "商户端-订单管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/order")
public class MerchantOrderController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = null;
        if (mtUser != null && mtUser.getMobile() != null) {
            staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (staffInfo == null) {
            return getFailureResult(1002, "该账号不是商户");
        } else if(staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0){
            orderListParam.setStoreId(staffInfo.getStoreId());
        }

        PaginationResponse orderData = orderService.getUserOrderList(orderListParam);
        return getSuccessResult(orderData);
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情")
    @Debounce
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request, @RequestBody OrderDetailParam orderDetailParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = orderDetailParam.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo = orderService.getMyOrderById(Integer.parseInt(orderDetailParam.getOrderId()));

        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @Debounce
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request, @RequestBody OrderDetailParam orderDetailParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "请先登录");
        }

        String orderId = orderDetailParam.getOrderId();
        if (orderId == null || StringUtil.isEmpty(orderId)) {
            return getFailureResult(201, "订单不能为空");
        }

        UserOrderDto orderDto = orderService.getOrderById(Integer.parseInt(orderId));
        if (orderDto == null) {
            return getFailureResult(201, "订单已不存在");
        }

        MtStaff staffInfo = staffService.queryStaffByUserId(mtUser.getId());
        if (staffInfo == null || orderDto.getStoreInfo() == null || !staffInfo.getStoreId().equals(orderDto.getStoreInfo().getId())) {
            return getFailureResult(201, "没有操作权限");
        }

        MtOrder orderInfo = orderService.cancelOrder(orderDto.getId(), "店员取消");
        return getSuccessResult(orderInfo);
    }
}
