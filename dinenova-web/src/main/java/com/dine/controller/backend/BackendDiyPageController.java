package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.dine.dto.AccountInfo;
import com.dine.dto.PageDto;
import com.dine.service.MtPageService;
import com.dine.util.TokenUtil;
import com.dine.util.diy.DefaultItems;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * DIY店铺首页装修管理类controller
 */
@Tag(name = "管理端-点餐页模板相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/diy/page")
public class BackendDiyPageController extends BaseController {

    private MtPageService pageService;

    @Debounce
    @GetMapping(value = "/getPage")
    @Operation(summary = "首页装修返回数据")
    @PreAuthorize("@pms.hasPermission('diy:index')")
    public ResponseObject toEditPage(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        // 店铺id
        Integer storeId = request.getParameter("storeId") == null ? null : Integer.parseInt(request.getParameter("storeId"));
        if (storeId == null) {
            return getFailureResult(1002, "请先选择店铺");
        }
        // 暂存 0、发布到当前店铺 1、发布到多门店 2、使用上次 3
        Integer type = request.getParameter("type") == null ? 0 : Integer.parseInt(request.getParameter("type"));

        PageDto pageDto = pageService.detail(storeId, type);
        Map<String, Object> result = new HashMap<>();
        result.put("jsonData", pageDto);
        result.put("defaultData", DefaultItems.getDefaultItems(""));
        return getSuccessResult(result);
    }

    @Debounce
    @RequestMapping(value = "/editPage", method = RequestMethod.POST)
    @Operation(summary = "首页装修保存数据")
    @PreAuthorize("@pms.hasPermission('diy:edit')")
    public ResponseObject editPage(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() < 1) {
            return getFailureResult(201, "平台方账户无操作权限");
        }

        // 店铺id：支持发布到多门店，当前门店
        // String storeId1 = request.getParameter("storeId");
        Integer storeId1 = (Integer) param.get("storeId");
        Integer storeId = storeId1 == null ? accountInfo.getStoreId() : storeId1;
        // 页面数据  大json
        // String params = request.getParameter("params") == null ? "" : request.getParameter("params");
        Object params = param.get("params");
        // string转json
        JSONObject jsonObject = new JSONObject(params);
        // 暂存 0、发布到当前店铺 1、发布到多门店 2、使用上次 3
        // String parameter = request.getParameter("type");
        String type = (String) param.get("type");
        // String type = StringUtils.isBlank(parameter) ? "" : parameter;

        if ("2".equals(type)) {
            storeId = accountInfo.getStoreId();
        }
        boolean result = pageService.edit(storeId, jsonObject.toString(), type, accountInfo);
        return getSuccessResult(result);
    }

}
