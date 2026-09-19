package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.param.MemberInfoParam;
import com.dine.service.MemberService;
import com.dine.service.MerchantService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtUser;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 收银台controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-收银台相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/cashier")
public class ClientCashierController extends BaseController {

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 获取会员信息
     */
    @Operation(summary = "查询会员信息")
    @Debounce
    @RequestMapping(value = "/memberInfo", method = RequestMethod.POST)
    public ResponseObject memberInfo(HttpServletRequest request,  @RequestBody MemberInfoParam memberInfoParam) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        String mobile = memberInfoParam.getMobile() == null ? "" : memberInfoParam.getMobile();
        if (StringUtil.isEmpty(mobile)) {
            return getFailureResult(201);
        }

        MtUser userInfo = memberService.queryMemberByMobile(merchantId, mobile);
        Map<String, Object> outParams = new HashMap<>();
        outParams.put("memberInfo", userInfo);

        return getSuccessResult(outParams);
    }
}
