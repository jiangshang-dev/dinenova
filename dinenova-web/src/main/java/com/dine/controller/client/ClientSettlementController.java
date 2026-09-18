package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.enums.YesOrNoEnum;
import com.dine.param.SettlementParam;
import com.dine.service.OrderService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 结算中心接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-订单结算相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/settlement")
public class ClientSettlementController extends BaseController {

    /**
     * 订单接口
     * */
    private OrderService orderService;

    /**
     * 订单结算
     */
    @Operation(summary = "提交订单结算")
    @Debounce
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject submit(HttpServletRequest request, @RequestBody SettlementParam param) throws BusinessCheckException {
        String isWechat = request.getHeader("isWechat") == null ? YesOrNoEnum.NO.getKey() : request.getHeader("isWechat");
        param.setIsWechat(isWechat);
        Map<String, Object> result = orderService.doSettle(request, param);
        return getSuccessResult(result);
    }
}
