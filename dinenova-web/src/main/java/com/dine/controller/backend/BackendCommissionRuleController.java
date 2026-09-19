package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.dto.CommissionRuleDto;
import com.dine.dto.ParamDto;
import com.dine.enums.CommissionTypeEnum;
import com.dine.param.CommissionRuleParam;
import com.dine.service.CommissionRuleService;
import com.dine.service.StoreService;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.constant.Constants;
import com.dine.enums.StatusEnum;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.repository.model.MtCommissionRule;
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
 * 分销提成规则管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-分销提成规则相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/commissionRule")
public class BackendCommissionRuleController extends BaseController {

    /**
     * 分销提成规则服务接口
     */
    private CommissionRuleService commissionRuleService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 规则列表查询
     *
     * @param request HttpServletRequest对象
     * @return 规则列表
     */
    @Operation(summary = "规则列表查询")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String target = request.getParameter("target");
        String type = request.getParameter("type");

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
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        if (StringUtil.isNotEmpty(target)) {
            params.put("target", target);
        }
        if (StringUtil.isNotEmpty(type)) {
            params.put("type", type);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("merchantId", accountInfo.getMerchantId());
        }
        if (storeId != null && storeId > 0) {
            params.put("storeId", storeId);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<MtCommissionRule> paginationResponse = commissionRuleService.queryDataByPagination(paginationRequest);

        // 分佣提成类型列表
        CommissionTypeEnum[] typeListEnum = CommissionTypeEnum.values();
        List<ParamDto> typeList = new ArrayList<>();
        for (CommissionTypeEnum enumItem : typeListEnum) {
            ParamDto paramDto = new ParamDto();
            paramDto.setKey(enumItem.getKey());
            paramDto.setName(enumItem.getValue());
            paramDto.setValue(enumItem.getKey());
            typeList.add(paramDto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("typeList", typeList);

        return getSuccessResult(result);
    }

    /**
     * 更新分销提成规则状态
     *
     * @return
     */
    @Operation(summary = "更新分销提成规则状态")
    @Debounce
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        CommissionRuleDto commissionRuleDto = commissionRuleService.queryCommissionRuleById(id);
        if (commissionRuleDto == null) {
            return getFailureResult(201);
        }

        String operator = accountInfo.getAccountName();

        CommissionRuleParam commissionRule = new CommissionRuleParam();
        commissionRule.setOperator(operator);
        commissionRule.setId(id);
        commissionRule.setStatus(status);
        commissionRuleService.updateCommissionRule(commissionRule);

        return getSuccessResult(true);
    }

    /**
     * 保存分销提成规则
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存分销提成规则")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody CommissionRuleParam params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.getId() == null ? "" : params.getId().toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            params.setStoreId(accountInfo.getStoreId());
        }
        if (StringUtil.isNotEmpty(id)) {
            commissionRuleService.updateCommissionRule(params);
        } else {
            commissionRuleService.addCommissionRule(params);
        }
        return getSuccessResult(true);
    }

    /**
     * 获取分销提成规则详情
     *
     * @param  id
     * @return
     */
    @Operation(summary = "获取分销提成规则详情")
    @Debounce
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('commission:rule:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        CommissionRuleDto commissionRule = commissionRuleService.queryCommissionRuleById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("commissionRule", commissionRule);

        return getSuccessResult(result);
    }
}
