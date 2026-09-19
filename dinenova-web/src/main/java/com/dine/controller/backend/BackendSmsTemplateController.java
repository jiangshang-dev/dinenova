package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.AccountInfo;
import com.dine.dto.SmsTemplateDto;
import com.dine.service.SmsTemplateService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtSmsTemplate;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信模板管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-短信模板相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/smsTemplate")
public class BackendSmsTemplateController extends BaseController {

    /**
     * 短信模板服务接口
     */
    private SmsTemplateService smsTemplateService;

    /**
     * 查询短信模板列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @Operation(summary = "查询短信模板列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('smsTemplate:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("content") == null ? "" : request.getParameter("content");
        String code = request.getParameter("code") == null ? "" : request.getParameter("code");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> searchParams = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            searchParams.put("merchantId", accountInfo.getMerchantId());
        }
        if (StringUtil.isNotEmpty(code)) {
            searchParams.put("code", code);
        }
        if (StringUtil.isNotEmpty(name)) {
            searchParams.put("name", name);
        }
        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtSmsTemplate> paginationResponse = smsTemplateService.querySmsTemplateListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 保存短信模板
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存短信模板")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('smsTemplate:edit')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody SmsTemplateDto smsTemplateDto) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        smsTemplateDto.setMerchantId(accountInfo.getMerchantId());
        smsTemplateService.saveSmsTemplate(smsTemplateDto);
        return getSuccessResult(true);
    }

    /**
     * 模板详情
     *
     * @param request
     * @return
     */
    @Operation(summary = "模板详情")
    @Debounce
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('smsTemplate:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Long id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtSmsTemplate mtSmsTemplate = smsTemplateService.querySmsTemplateById(id.intValue());

        Map<String, Object> result = new HashMap();
        result.put("smsTemplate", mtSmsTemplate);

        return getSuccessResult(result);
    }

    /**
     * 删除短信模板
     *
     * @param request
     * @return
     */
    @Operation(summary = "删除短信模板")
    @Debounce
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('smsTemplate:edit')")
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        String operator = accountInfo.getAccountName();
        smsTemplateService.deleteTemplate(id, operator);

        return getSuccessResult(true);
    }
}
