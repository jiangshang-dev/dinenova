package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.AccountInfo;
import com.dine.service.CouponService;
import com.dine.service.MemberService;
import com.dine.service.SendLogService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtSendLog;
import com.dine.repository.model.MtUser;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 卡券发放记录管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-卡券发放相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/sendLog")
public class BackendSendLogController extends BaseController {

    /**
     * 发送记录服务接口
     */
    private SendLogService sendLogService;

    /**
     * 会员接口服务
     * */
    private MemberService memberService;

    /**
     * 卡券服务接口
     * */
    private CouponService couponService;

    /**
     * 查询发券记录列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @Operation(summary = "查询发券记录列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String userId = request.getParameter("userId") == null ? "" : request.getParameter("userId");
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String couponId = request.getParameter("couponId") == null ? "" : request.getParameter("couponId");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(status)) {
            searchParams.put("status", status);
        }

        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }

        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            searchParams.put("storeId", accountInfo.getStoreId());
        }

        if (StringUtil.isNotEmpty(userId)) {
            searchParams.put("userId", userId);
        }

        if (StringUtil.isNotEmpty(couponId)) {
            searchParams.put("couponId", couponId);
        }

        if (StringUtil.isNotEmpty(mobile)) {
            MtUser userInfo = memberService.queryMemberByMobile(accountInfo.getMerchantId(), mobile);
            if (userInfo != null) {
                searchParams.put("userId", userInfo.getId().toString());
            } else {
                searchParams.put("userId", "0");
            }
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtSendLog> paginationResponse = sendLogService.querySendLogListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 废除用户卡券
     *
     * @param request
     * @param id 日志ID
     * @return
     */
    @Operation(summary = "废除用户卡券")
    @Debounce
    @RequestMapping(value = "/removeUserCoupon/{id}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject removeUserCoupon(HttpServletRequest request, @PathVariable("id") Long id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        if (id == null) {
            return getFailureResult(201, "系统参数有误");
        }

        MtSendLog sendLog = sendLogService.querySendLogById(id);
        if (sendLog == null) {
            return getFailureResult(201, "系统参数有误");
        }

        couponService.removeUserCoupon(id, sendLog.getUuid(), accountInfo.getAccountName());
        return getSuccessResult(true);
    }
}
