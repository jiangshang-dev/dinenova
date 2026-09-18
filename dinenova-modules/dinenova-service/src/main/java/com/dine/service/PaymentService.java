package com.dine.service;

import com.dine.dto.UserOrderDto;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtOrder;
import com.dine.repository.model.MtUser;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付相关业务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public interface PaymentService {

    /**
     * 创建预支付订单
     *
     * @param userInfo 会员信息
     * @param orderInfo 订单信息
     * @param payAmount 支付金额
     * @param authCode 付款码
     * @param giveAmount 赠送金额
     * @param ip 支付IP地址
     * @param platform 支付平台
     * @param isWechat 是否微信客户端
     * @return
     * */
    ResponseObject createPrepayOrder(MtUser userInfo, MtOrder orderInfo, Integer payAmount, String authCode, Integer giveAmount, String ip, String platform, String isWechat) throws BusinessCheckException;

    /**
     * 支付回调
     *
     * @param orderInfo 订单信息
     * @return
     * */
    Boolean paymentCallback(UserOrderDto orderInfo) throws BusinessCheckException;

    /**
     * 订单支付
     *
     * @param request 请求参数
     * @return
     * */
    Map<String, Object> doPay(HttpServletRequest request) throws BusinessCheckException;

}