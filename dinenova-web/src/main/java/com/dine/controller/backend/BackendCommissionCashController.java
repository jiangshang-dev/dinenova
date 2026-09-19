package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.dto.CommissionCashDto;
import com.dine.dto.ParamDto;
import com.dine.enums.CommissionCashStatusEnum;
import com.dine.service.CommissionCashService;
import com.dine.service.StoreService;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.constant.Constants;
import com.dine.enums.StatusEnum;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.param.backend.CommissionCashRequest;
import com.dine.param.backend.CommissionSettleConfirmRequest;
import com.dine.repository.model.MtStore;
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
 * 分销提成提现管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-分销提成提现相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionCash")
public class BackendCommissionCashController extends BaseController {

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 分销提成提现业务接口
     */
    private CommissionCashService commissionCashService;

    /**
     * 分销提成提现记录列表
     *
     * @param request HttpServletRequest对象
     * @return 分销提成记录
     */
    @Operation(summary = "分销提成提现记录列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String realName = request.getParameter("realName");
        String mobile = request.getParameter("mobile");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");
        String uuid = request.getParameter("uuid");
        String startTime = request.getParameter("startTime") == null ? "" : request.getParameter("startTime");
        String endTime = request.getParameter("endTime") == null ? "" : request.getParameter("endTime");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId;
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        } else {
            storeId = accountInfo.getStoreId();
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(realName)) {
            params.put("realName", realName);
        }
        if (StringUtil.isNotEmpty(mobile)) {
            params.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        if (StringUtil.isNotEmpty(uuid)) {
            params.put("uuid", uuid);
        }
        if (StringUtil.isNotEmpty(startTime)) {
            params.put("startTime", startTime);
        }
        if (StringUtil.isNotEmpty(endTime)) {
            params.put("endTime", endTime);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<CommissionCashDto> paginationResponse = commissionCashService.queryCommissionCashByPagination(paginationRequest);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            paramsStore.put("storeId", accountInfo.getStoreId().toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);

        // 状态列表
        CommissionCashStatusEnum[] statusListEnum = CommissionCashStatusEnum.values();
        List<ParamDto> statusList = new ArrayList<>();
        for (CommissionCashStatusEnum enumItem : statusListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            statusList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);
        result.put("storeList", storeList);
        result.put("statusList", statusList);

        return getSuccessResult(result);
    }

    /**
     * 获取分销提成记录详情
     *
     * @param  id
     * @return
     */
    @Operation(summary = "获取分销提成提现详情")
    @Debounce
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        CommissionCashDto commissionCash = commissionCashService.queryCommissionCashById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("commissionCash", commissionCash);

        return getSuccessResult(result);
    }

    /**
     * 修改分销提成提现
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "修改分销提成提现")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject save(HttpServletRequest request, @RequestBody CommissionCashRequest commissionCashRequest) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        commissionCashRequest.setOperator(accountDto.getAccountName());
        commissionCashService.updateCommissionCash(commissionCashRequest);

        return getSuccessResult(true);
    }

    /**
     * 结算确认
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "结算确认")
    @Debounce
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject confirm(HttpServletRequest request, @RequestBody CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        requestParam.setOperator(accountDto.getAccountName());
        if (accountDto.getMerchantId() != null && accountDto.getMerchantId() > 0) {
            requestParam.setMerchantId(accountDto.getMerchantId());
        }
        commissionCashService.confirmCommissionCash(requestParam);

        return getSuccessResult(true);
    }

    /**
     * 取消结算
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "取消结算")
    @Debounce
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:cash:index')")
    public ResponseObject cancel(HttpServletRequest request, @RequestBody CommissionSettleConfirmRequest requestParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountDto.getMerchantId() != null && accountDto.getMerchantId() > 0) {
            requestParam.setMerchantId(accountDto.getMerchantId());
        }

        requestParam.setOperator(accountDto.getAccountName());
        commissionCashService.cancelCommissionCash(requestParam);

        return getSuccessResult(true);
    }
}
