package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.alibaba.fastjson.JSONObject;
import com.dine.dto.UserInfo;
import com.dine.enums.SettingTypeEnum;
import com.dine.service.MerchantService;
import com.dine.service.MessageService;
import com.dine.service.SettingService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtMessage;
import com.dine.repository.model.MtSetting;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 消息相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-消息相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/message")
public class ClientMessageController extends BaseController {

    /**
     * 消息服务接口
     */
    private MessageService messageService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 查询最新一条未读消息
     *
     * @param request Request对象
     */
    @Operation(summary = "查询最新一条未读消息")
    @Debounce
    @RequestMapping(value = "/getOne", method = RequestMethod.GET)
    public ResponseObject getOne(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getSuccessResult(false);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);
        if (null == mtUser) {
            return getSuccessResult(false);
        }

        MtMessage messageInfo = messageService.getOne(mtUser.getId());
        Map<String, Object> outParams = new HashMap();
        if (messageInfo != null) {
            outParams.put("msgId", messageInfo.getId());
            outParams.put("title", messageInfo.getTitle());
            outParams.put("content", messageInfo.getContent());
        }

        ResponseObject responseObject = getSuccessResult(outParams);

        return getSuccessResult(responseObject.getData());
    }

    /**
     * 将消息置为已读
     */
    @Operation(summary = "将消息置为已读")
    @Debounce
    @RequestMapping(value = "/readed", method = RequestMethod.GET)
    public ResponseObject readed(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        Integer msgId =  request.getParameter("msgId") == null ? 0 :Integer.parseInt(request.getParameter("msgId"));

        if (null == mtUser) {
            return getSuccessResult(false);
        }

        messageService.readMessage(msgId);

        ResponseObject responseObject = getSuccessResult(true);
        return getSuccessResult(responseObject.getData());
    }

    /**
     * 微信推送消息
     */
    @Operation(summary = "微信推送消息")
    @Debounce
    @RequestMapping(value = "/wxPush", method = RequestMethod.GET)
    public String wxPush(HttpServletRequest request) {
        String echostr =  request.getParameter("echostr") == null ? "" : request.getParameter("echostr");

        if (StringUtil.isNotEmpty(echostr)) {
            return echostr;
        }

        return "";
    }

    /**
     * 微信订阅消息模板
     */
    @Operation(summary = "微信订阅消息模板")
    @Debounce
    @RequestMapping(value = "/getSubTemplate", method = RequestMethod.GET)
    public ResponseObject getSubTemplate(HttpServletRequest request) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo");
        String keys =  request.getParameter("keys") == null ? "" :request.getParameter("keys");

        List<String> dataList = new ArrayList<>();
        Integer merchantId = merchantService.getMerchantId(merchantNo);
        List<MtSetting> settingList = settingService.getSettingList(merchantId, SettingTypeEnum.SUB_MESSAGE.getKey());
        for (MtSetting mtSetting : settingList) {
            if (keys.indexOf(mtSetting.getName()) >= 0) {
                try {
                    JSONObject jsonObject = JSONObject.parseObject(mtSetting.getValue());
                    if (jsonObject != null) {
                        String templateId = jsonObject.get("templateId").toString();
                        if (StringUtil.isNotEmpty(templateId)) {
                            dataList.add(templateId);
                        }
                    }
                } catch (Exception e) {
                    // empty
                }
            }
        }

        ResponseObject responseObject = getSuccessResult(dataList);
        return getSuccessResult(responseObject.getData());
    }
}
