package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.dine.dto.AccountInfo;
import com.dine.dto.ParamDto;
import com.dine.dto.SubMessageDto;
import com.dine.enums.SettingTypeEnum;
import com.dine.enums.StatusEnum;
import com.dine.enums.WxMessageEnum;
import com.dine.service.SettingService;
import com.dine.util.CommonUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtSetting;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.env.Environment;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 小程序订阅消息管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-小程序订阅消息相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/subMessage")
public class BackendSubMessageController extends BaseController {

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 系统环境变量
     * */
    private Environment env;

    /**
     * 订阅消息模板列表
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "订阅消息模板列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('subMessage:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        String title = request.getParameter("title") == null ? "" : request.getParameter("title");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        List<SubMessageDto> dataList = new ArrayList<>();
        for (WxMessageEnum wxMessageEnum : WxMessageEnum.values()) {
            SubMessageDto e = new SubMessageDto();
            MtSetting setting = settingService.querySettingByName(accountInfo.getMerchantId(), SettingTypeEnum.SUB_MESSAGE.getKey(), wxMessageEnum.getKey());
            e.setKey(wxMessageEnum.getKey());
            e.setTitle(wxMessageEnum.getValue());
            JSONObject jsonObject = null;
            if (setting != null) {
                try {
                    jsonObject = JSONObject.parseObject(setting.getValue());
                } catch (Exception ex) {
                    // empty
                }
                if (jsonObject != null) {
                    String templateId = jsonObject.get("templateId").toString();
                    String tid = jsonObject.get("tid").toString();
                    JSONArray paramArray = (JSONArray) JSONObject.parse(jsonObject.get("params").toString());
                    if (StringUtil.isEmpty(templateId) || StringUtil.isEmpty(tid) || paramArray.size() < 1) {
                        jsonObject = null;
                    }
                }
            }

            if (setting != null && jsonObject != null) {
                e.setStatus(setting.getStatus());
            } else {
                e.setStatus(StatusEnum.FORBIDDEN.getKey());
            }

            if (StringUtil.isNotEmpty(status)) {
                if (e.getStatus().equals(status)) {
                    dataList.add(e);
                }
            } else if(StringUtil.isNotEmpty(title)) {
                if (e.getTitle().indexOf(title) >= 0) {
                    dataList.add(e);
                }
            } else {
                dataList.add(e);
            }
        }

        Map<String, Object> result = new HashMap();
        result.put("dataList", dataList);

        return getSuccessResult(result);
    }

    /**
     * 订阅消息模板详情
     *
     * @param key 消息键值
     * @return
     */
    @Operation(summary = "订阅消息模板详情")
    @Debounce
    @RequestMapping(value = "/info/{key}", method = RequestMethod.GET)
    @PreAuthorize("@pms.hasPermission('subMessage:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("key") String key) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        Map<String, Object> result = new HashMap();

        String name = WxMessageEnum.getValue(key);
        if (StringUtil.isNotEmpty(name)) {
            MtSetting mtSetting = settingService.querySettingByName(accountInfo.getMerchantId(), SettingTypeEnum.SUB_MESSAGE.getKey(), key);
            JSONObject jsonObject = null;
            try {
                if (mtSetting != null && mtSetting.getValue().indexOf('}') > 0) {
                    jsonObject = JSONObject.parseObject(mtSetting.getValue());
                }
                String templateId = "";
                String tid = "";
                JSONArray paramArray = null;

                if (jsonObject != null) {
                    templateId = jsonObject.get("templateId").toString();
                    tid = jsonObject.get("tid").toString();
                    paramArray = (JSONArray) JSONObject.parse(jsonObject.get("params").toString());
                }

                List<ParamDto> params = new ArrayList<>();
                String tplConfigJson = env.getProperty("weixin.subMessage." + key);
                if (StringUtil.isNotEmpty(tplConfigJson)) {
                    JSONArray jsonArray = (JSONArray)JSONObject.parse(tplConfigJson);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        ParamDto dto = new ParamDto();
                        dto.setKey(obj.get("key").toString());
                        // 解决中文乱码
                        String pName = obj.get("name").toString();
                        if (!CommonUtil.isUtf8(pName) || CommonUtil.isErrCode(pName)) {
                            pName = new String(obj.get("name").toString().getBytes("ISO8859-1"), "UTF-8");
                        }
                        dto.setName(pName);
                        if (paramArray != null) {
                            dto.setValue("");
                            for (int j = 0; j < paramArray.size(); j++) {
                                 JSONObject paraObj = paramArray.getJSONObject(j);
                                 if (paraObj.get("key").toString().equals(obj.get("key").toString())) {
                                     dto.setValue(paraObj.get("value") == null ? "" : paraObj.get("value").toString());
                                 }
                            }
                        } else {
                            dto.setValue("");
                        }
                        params.add(dto);
                    }
                }

                result.put("params", params);
                result.put("name", name);
                result.put("key", key);
                result.put("templateId", templateId);
                result.put("tid", tid);
                return getSuccessResult(result);
            } catch (Exception e) {
                return getFailureResult(201, "操作失败");
            }
        }

        return getFailureResult(201, "操作失败");
    }

    /**
     * 保存订阅消息模板
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存订阅消息模板")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("@pms.hasPermission('subMessage:edit')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String token = request.getHeader("Access-Token");
        String key = param.get("key").toString();
        String templateId = param.get("templateId").toString();
        String tid = param.get("tid").toString();
        List<LinkedHashMap> paramData = (List) param.get("params");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(201, "请使用商户账号操作");
        }

        SubMessageDto subMessageDto = new SubMessageDto();
        subMessageDto.setKey(key);
        subMessageDto.setTemplateId(templateId);
        subMessageDto.setTid(tid);
        subMessageDto.setStatus(StatusEnum.ENABLED.getKey());

        try {
            String tplConfigJson = env.getProperty("weixin.subMessage." + key);
            JSONArray jsonArray = (JSONArray) JSONObject.parse(tplConfigJson);
            List<ParamDto> params = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String oKey = obj.get("key").toString();
                String value = "";
                for (LinkedHashMap paraItem : paramData) {
                     String pKey = paraItem.get("key").toString();
                     if (pKey.equals(oKey)) {
                         value = paraItem.get("value").toString();
                         break;
                     }
                }

                ParamDto para = new ParamDto();
                String name = obj.get("name").toString();
                if (!CommonUtil.isUtf8(name) || CommonUtil.isErrCode(name)) {
                    name = new String(name.getBytes("ISO8859-1"), "UTF-8");
                }
                para.setName(name);
                para.setKey(obj.get("key").toString());
                para.setValue(value);
                params.add(para);
            }

            subMessageDto.setParams(params);
            String json = JSONObject.toJSONString(subMessageDto);

            // 保存配置
            settingService.removeSetting(accountInfo.getMerchantId(), SettingTypeEnum.SUB_MESSAGE.getKey(), key);
            MtSetting info = new MtSetting();
            info.setMerchantId(accountInfo.getMerchantId());
            info.setType(SettingTypeEnum.SUB_MESSAGE.getKey());
            info.setName(key);
            info.setValue(json);
            info.setMerchantId(accountInfo.getMerchantId());
            info.setStoreId(0);
            String description = WxMessageEnum.getValue(key);
            info.setDescription(description);
            info.setOperator(accountInfo.getAccountName());
            info.setUpdateTime(new Date());
            settingService.saveSetting(info);
        }  catch (Exception e) {
            return getFailureResult(201, "操作失败");
        }

        return getSuccessResult(true);
    }
}
