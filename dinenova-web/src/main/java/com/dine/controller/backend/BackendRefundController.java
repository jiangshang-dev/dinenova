package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.*;
import com.dine.enums.RefundStatusEnum;
import com.dine.enums.RefundTypeEnum;
import com.dine.service.AccountService;
import com.dine.service.MemberService;
import com.dine.service.OrderService;
import com.dine.service.RefundService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtUser;
import com.dine.repository.model.TAccount;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 售后管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-售后订单相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/refund")
public class BackendRefundController extends BaseController {

    /**
     * 售后服务接口
     * */
    private RefundService refundService;

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 后台账户服务接口
     */
    private AccountService accountService;

    /**
     * 会员接口服务
     * */
    private MemberService memberService;

    /**
     * 退款列表查询
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "退款列表查询")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String orderSn = request.getParameter("orderSn");
        String mobile = request.getParameter("mobile");
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String startTime = request.getParameter("startTime") == null ? "" : request.getParameter("startTime");
        String endTime = request.getParameter("endTime") == null ? "" : request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (account.getMerchantId() != null && account.getMerchantId() > 0) {
            params.put("merchantId", account.getMerchantId());
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(orderSn)) {
            UserOrderDto orderInfo = orderService.getOrderByOrderSn(orderSn);
            if (orderInfo != null) {
                params.put("orderId", orderInfo.getId().toString());
            } else {
                params.put("orderId", "0");
            }
        }
        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(account.getMerchantId(), mobile);
            if (userInfo != null) {
                userId = userInfo.getId().toString();
            } else {
                userId = "0";
            }
        }
        if (StringUtil.isNotEmpty(userId)) {
            params.put("userId", userId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        if (StringUtil.isNotEmpty(startTime)) {
            params.put("startTime", startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            params.put("endTime", endTime);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<RefundDto> paginationResponse = refundService.getRefundListByPagination(paginationRequest);

        // 售后状态列表
        RefundStatusEnum[] statusListEnum = RefundStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (RefundStatusEnum enumItem : statusListEnum) {
             ParamDto paramDto = new ParamDto();
             paramDto.setKey(enumItem.getKey());
             paramDto.setName(enumItem.getValue());
             paramDto.setValue(enumItem.getKey());
             statusList.add(paramDto);
        }

        // 售后类型列表
        RefundTypeEnum[] refundTypeEnums = RefundTypeEnum.values();
        List<ParamDto> refundTypeList = new ArrayList<>();
        for (RefundTypeEnum enumItem : refundTypeEnums) {
             ParamDto paramDto = new ParamDto();
             paramDto.setKey(enumItem.getKey());
             paramDto.setName(enumItem.getValue());
             paramDto.setValue(enumItem.getKey());
             refundTypeList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("statusList", statusList);
        result.put("refundTypeList", refundTypeList);

        return getSuccessResult(result);
    }

    /**
     * 查询退款详情
     * @param request HttpServletRequest对象
     * @return
     * */
    @Operation(summary = "查询退款详情")
    @Debounce
    @RequestMapping(value = "/info/{refundId}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("refundId") Integer refundId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        RefundDto refundInfo = refundService.getRefundById(refundId);
        UserOrderDto orderInfo = null;
        if (refundInfo != null) {
            orderInfo = orderService.getOrderById(refundInfo.getOrderId());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("refundInfo", refundInfo);
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }

    /**
     * 保存售后订单
     * @return
     */
    @Operation(summary = "保存售后订单")
    @Debounce
    @RequestMapping(value = "save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:edit')")
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer refundId = param.get("refundId") == null ? 0 : Integer.parseInt(param.get("refundId").toString());
        String status = param.get("status") == null ? "" : param.get("status").toString();
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String rejectReason = param.get("rejectReason") == null ? "" : param.get("rejectReason").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        String operator = accountInfo.getAccountName();
        if (status.equals(RefundStatusEnum.REJECT.getKey())) {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(operator);
            dto.setStatus(RefundStatusEnum.REJECT.getKey());
            dto.setRemark(remark);
            dto.setRejectReason(rejectReason);
            refundService.updateRefund(dto);
        } else {
            RefundDto dto = new RefundDto();
            dto.setId(refundId);
            dto.setOperator(operator);
            dto.setStatus(RefundStatusEnum.APPROVED.getKey());
            dto.setRemark(remark);
            refundService.agreeRefund(dto);
        }
        return getSuccessResult(true);
    }

    /**
     * 发起退款
     * @return
     */
    @Operation(summary = "发起退款")
    @Debounce
    @RequestMapping(value = "doRefund", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('refund:edit')")
    public ResponseObject doRefund(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer orderId = param.get("orderId") == null ? 0 : Integer.parseInt(param.get("orderId").toString());
        String remark = param.get("remark") == null ? "" : param.get("remark").toString();
        String refundAmount = param.get("refundAmount") == null ? "" : param.get("refundAmount").toString();
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (orderId <= 0 || StringUtil.isEmpty(refundAmount)) {
            return getFailureResult(201, "参数有误，发起退款失败");
        }
        Boolean result = refundService.doRefund(orderId, refundAmount, remark, accountInfo);
        if (result) {
            return getSuccessResult(true);
        } else {
            return getFailureResult(201, "退款失败");
        }
    }
}
