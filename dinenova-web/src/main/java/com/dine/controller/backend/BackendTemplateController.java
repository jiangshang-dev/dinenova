package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dine.dto.AccountInfo;
import com.dine.service.TemplateService;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.constant.Constants;
import com.dine.enums.StatusEnum;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.repository.model.Template;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点餐页模板管理类controller
 */
@Tag(name = "管理端-点餐页模板相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/template")
public class BackendTemplateController extends BaseController {

    @Autowired
    private TemplateService templateService;

    /**
     * 模板查询列表
     *
     * @param request HttpServletRequest对象
     * @return 模板查询列表
     */
    @Operation(summary = "模板列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('template:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {

        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        // String title = request.getParameter("title");
        // String status = request.getParameter("status");
        // String merchantId = request.getParameter("merchantId");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        Integer storeId;
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        // if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
        //     params.put("merchantId", accountInfo.getMerchantId());
        // }
        // if (StringUtil.isNotEmpty(merchantId)) {
        //     params.put("title", merchantId);
        // }
        paginationRequest.setSearchParams(params);
        PaginationResponse<Template> paginationResponse = templateService.queryTemplateListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 保存模板配置
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存模板配置")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('template:add')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer rawId = parseInt(params.get("id"));
        Integer storeId = parseInt(params.get("storeId"));
        Integer merchantId = parseInt(params.get("merchantId"));
        String color = params.get("color") == null ? "" : params.get("color").toString();
        String templateValue = params.get("templateValue") == null ? "0" : params.get("templateValue").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(201, "平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        Template existing = null;
        if (rawId != null && rawId > 0) {
            existing = templateService.getById(rawId);
        }
        if (existing == null) {
            Integer sid = (storeId != null && storeId > 0) ? storeId : rawId;
            if (sid != null && sid > 0) {
                LambdaQueryWrapper<Template> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Template::getStoreId, sid);
                existing = templateService.getOne(queryWrapper);
            }
        }

        if (existing != null) {
            existing.setColor(color);
            existing.setTemplateValue(templateValue);
            existing.setOperator(accountInfo.getAccountName());
            existing.setUpdateTime(LocalDateTime.now());
            if (existing.getMerchantId() <= 0) {
                existing.setMerchantId(accountInfo.getMerchantId());
            }
            templateService.updateTemplate(existing);
        } else {
            Template info = new Template();
            info.setOperator(accountInfo.getAccountName());
            info.setColor(color);
            info.setTemplateValue(templateValue);
            info.setStoreId(storeId != null && storeId > 0 ? storeId : (rawId == null ? 0 : rawId));
            info.setMerchantId(merchantId != null && merchantId > 0 ? merchantId : accountInfo.getMerchantId());
            info.setCreateTime(LocalDateTime.now());
            info.setUpdateTime(LocalDateTime.now());
            templateService.addTemplate(info);
        }
        return getSuccessResult(true);
    }

    /**
     * 模板配置详情
     *
     * @param storeId
     * @return
     */
    @Operation(summary = "模板配置详情")
    @RequestMapping(value = "/info/{storeId}", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('template:info')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("storeId") Integer storeId) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Template template = templateService.queryTemplateById(storeId);

        Map<String, Object> result = new HashMap<>();
        result.put("template", template);
        return getSuccessResult(result);
    }

    @Operation(summary = "删除点餐页模板")
    @Debounce
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public ResponseObject delete(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        templateService.removeById(id);
        return getSuccessResult("");
    }

    @Operation(summary = "更新模板状态")
    @Debounce
    @RequestMapping(value = "/updateStatus/{id}/{status}", method = RequestMethod.GET)
    public ResponseObject updateStatus(HttpServletRequest request, @PathVariable("id") Integer id, @PathVariable("status") String status) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        LambdaUpdateWrapper<Template> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Template::getId, id);
        wrapper.set(Template::getStatus, status);
        templateService.update(wrapper);
        return getSuccessResult("修改状态成功");
    }

    private Integer parseInt(Object value) {
        if (value == null || StringUtil.isEmpty(value.toString())) {
            return null;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
