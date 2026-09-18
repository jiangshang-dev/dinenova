package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.OrderDto;
import com.dine.dto.UserInfo;
import com.dine.dto.UserOrderDto;
import com.dine.enums.OrderStatusEnum;
import com.dine.param.OrderListParam;
import com.dine.service.OrderService;
import com.dine.service.TableService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtOrder;
import com.dine.repository.model.MtTable;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/order")
public class ClientOrderController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 桌码服务接口
     */
    private TableService tableService;

    /**
     * 获取我的订单列表
     */
    @Operation(summary = "获取我的订单列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody OrderListParam orderListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer tableId = request.getHeader("tableId") == null ? 0 : Integer.parseInt(request.getHeader("tableId"));
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }
        orderListParam.setUserId(userInfo.getId());
        if (tableId > 0) {
            MtTable mtTable = tableService.queryTableById(tableId);
            if (mtTable != null) {
                orderListParam.setTableCode(mtTable.getCode());
                orderListParam.setUserId(null);
                orderListParam.setStatus(OrderStatusEnum.CREATED.getKey());
            }
        }

        PaginationResponse orderData = orderService.getUserOrderList(orderListParam);
        return getSuccessResult(orderData);
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情")
    @Debounce
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        if (userInfo == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto orderInfo = orderService.getMyOrderById(Integer.parseInt(orderId));
        return getSuccessResult(orderInfo);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @Debounce
    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cancel(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
        if (!order.getUserId().equals(mtUser.getId())) {
            return getFailureResult(2000, "订单信息有误");
        }

        MtOrder orderInfo = orderService.cancelOrder(order.getId(), "会员取消");

        return getSuccessResult(orderInfo);
    }

    /**
     * 确认收货
     */
    @Operation(summary = "确认收货")
    @Debounce
    @RequestMapping(value = "/receipt", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject receipt(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (mtUser == null) {
            return getFailureResult(1001, "用户未登录");
        }

        String orderId = request.getParameter("orderId");
        if (StringUtil.isEmpty(orderId)) {
            return getFailureResult(2000, "订单不能为空");
        }

        UserOrderDto order = orderService.getOrderById(Integer.parseInt(orderId));
        if (!order.getUserId().equals(mtUser.getId())) {
            return getFailureResult(2000, "订单信息有误");
        }

        OrderDto reqDto = new OrderDto();
        reqDto.setId(Integer.parseInt(orderId));
        reqDto.setStatus(OrderStatusEnum.RECEIVED.getKey());
        MtOrder orderInfo = orderService.updateOrder(reqDto);

        return getSuccessResult(orderInfo);
    }

    /**
     * 获取待办订单数量
     */
    @Operation(summary = "获取待办订单数量")
    @Debounce
    @RequestMapping(value = "/todoCounts", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject todoCounts(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);

        Map<String, Object> result = new HashMap<>();

        if (userInfo != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("status", OrderStatusEnum.CREATED.getKey());
            param.put("user_id", userInfo.getId());
            List<MtOrder> data = orderService.getOrderListByParams(param);
            result.put("toPay", data.size());
        }

        return getSuccessResult(result);
    }
}
