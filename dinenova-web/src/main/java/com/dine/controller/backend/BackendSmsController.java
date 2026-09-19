package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.AccountInfo;
import com.dine.enums.SettingTypeEnum;
import com.dine.enums.SmsSettingEnum;
import com.dine.service.SendSmsService;
import com.dine.service.SettingService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtSetting;
import com.dine.repository.model.MtSmsSendedLog;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-短信相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/smsManager")
public class BackendSmsController extends BaseController {

    /**
     * 短信发送接口
     */
    private SendSmsService sendSmsService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 查询已发短信列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @Operation(summary = "查询已发短信列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String mobile = request.getParameter("mobile") == null ? "" : request.getParameter("mobile");
        String content = request.getParameter("content") == null ? "" : request.getParameter("content");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> searchParams = new HashMap<>();
        if (StringUtil.isNotEmpty(mobile)) {
            searchParams.put("mobile", mobile);
        }
        if (StringUtil.isNotEmpty(content)) {
            searchParams.put("content", content);
        }

        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<MtSmsSendedLog> paginationResponse = sendSmsService.querySmsListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 获取短信设置
     *
     * @param request
     * @return
     */
    @Operation(summary = "获取短信设置")
    @Debounce
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('smsTemplate:edit')")
    public ResponseObject setting(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        List<MtSetting> settingList = settingService.getSettingList(accountInfo.getMerchantId(), SettingTypeEnum.SMS_CONFIG.getKey());

        String isClose = "0";
        String accessKeyId = "";
        String accessKeySecret = "";
        String signName = "";
        for (MtSetting setting : settingList) {
            if (StringUtil.isNotEmpty(setting.getValue())) {
                if (setting.getName().equals(SmsSettingEnum.IS_CLOSE.getKey())) {
                    isClose = setting.getValue();
                } else if (setting.getName().equals(SmsSettingEnum.ACCESS_KEY_ID.getKey())) {
                    accessKeyId = setting.getValue();
                } else if (setting.getName().equals(SmsSettingEnum.ACCESS_KEY_SECRET.getKey())) {
                    accessKeySecret = setting.getValue();
                } else if (setting.getName().equals(SmsSettingEnum.SIGN_NAME.getKey())) {
                    signName = setting.getValue();
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isClose", isClose);
        result.put("accessKeyId", accessKeyId);
        result.put("accessKeySecret", accessKeySecret);
        result.put("signName", signName);

        return getSuccessResult(result);
    }

    /**
     * 保存短信设置
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存短信设置")
    @Debounce
    @RequestMapping(value = "/saveSetting", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('smsTemplate:edit')")
    public ResponseObject saveSetting(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String isClose = param.get("isClose") != null ? param.get("isClose").toString() : null;
        String accessKeyId = param.get("accessKeyId") != null ? param.get("accessKeyId").toString() : null;
        String accessKeySecret = param.get("accessKeySecret") != null ? param.get("accessKeySecret").toString() : null;
        String signName = param.get("signName") != null ? param.get("signName").toString() : null;

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(201, "平台方帐号无法执行该操作，请使用商户帐号操作");
        }

        SmsSettingEnum[] settingList = SmsSettingEnum.values();
        for (SmsSettingEnum setting : settingList) {
            MtSetting mtSetting = new MtSetting();
            mtSetting.setType(SettingTypeEnum.SMS_CONFIG.getKey());
            mtSetting.setName(setting.getKey());
            if (setting.getKey().equals(SmsSettingEnum.IS_CLOSE.getKey())) {
                mtSetting.setValue(isClose);
            } else if (setting.getKey().equals(SmsSettingEnum.ACCESS_KEY_ID.getKey())) {
                mtSetting.setValue(accessKeyId);
            } else if (setting.getKey().equals(SmsSettingEnum.ACCESS_KEY_SECRET.getKey())) {
                mtSetting.setValue(accessKeySecret);
            } else if (setting.getKey().equals(SmsSettingEnum.SIGN_NAME.getKey())) {
                mtSetting.setValue(signName);
            }
            mtSetting.setDescription(setting.getValue());
            mtSetting.setOperator(accountInfo.getAccountName());
            mtSetting.setUpdateTime(new Date());
            mtSetting.setMerchantId(accountInfo.getMerchantId());
            mtSetting.setStoreId(0);
            settingService.saveSetting(mtSetting);
        }

        return getSuccessResult(true);
    }
}
