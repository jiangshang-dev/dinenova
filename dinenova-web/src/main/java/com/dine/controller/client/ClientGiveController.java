package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.GiveDto;
import com.dine.dto.UserInfo;
import com.dine.param.GiveListParam;
import com.dine.param.GiveParam;
import com.dine.service.GiveService;
import com.dine.service.MemberService;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
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
 * 卡券转赠controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-卡券转赠相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/give")
public class ClientGiveController extends BaseController {

    /**
     * 转赠服务接口
     */
    private GiveService giveService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 转赠卡券
     *
     * @param giveParam Request对象
     */
    @Operation(summary = "转赠卡券")
    @Debounce
    @RequestMapping(value = "/doGive", method = RequestMethod.POST)
    public ResponseObject doGive(HttpServletRequest request, @RequestBody GiveParam giveParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null) {
            return getFailureResult(1001);
        }
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        giveParam.setUserId(mtUser.getId());
        giveParam.setStoreId(mtUser.getStoreId());
        giveParam.setMerchantId(mtUser.getMerchantId());

        try {
            /*
            String vcode = param.get("vcode") == null ? "" : param.get("vcode").toString();
            if (StringUtil.isEmpty(vcode)) {
                return getFailureResult(3001, "验证码不能为空");
            }
            MtVerifyCode mtVerifyCode = verifyCodeService.checkVerifyCode(mtUser.getMobile(), vcode);
            if (null != mtVerifyCode) {
                verifyCodeService.updateValidFlag(mtVerifyCode.getId(),"1");
            } else {
                return getFailureResult(3002, "验证码有误");
            }*/
            ResponseObject result = giveService.addGive(giveParam);

            return getSuccessResult(result.getData());
        } catch (BusinessCheckException e) {
            return getFailureResult(3008, e.getMessage());
        }
    }

    /**
     * 查询转赠记录
     *
     * @param request Request对象
     */
    @Operation(summary = "查询转赠记录")
    @Debounce
    @RequestMapping(value = "/giveLog", method = RequestMethod.POST)
    public ResponseObject giveLog(HttpServletRequest request, @RequestBody GiveListParam giveListParam) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");

        if (StringUtil.isEmpty(token)) {
            return getFailureResult(1001);
        }

        UserInfo mtUser = TokenUtil.getUserInfoByToken(token);

        if (null == mtUser) {
            return getFailureResult(1001);
        }

        String mobile = giveListParam.getMobile() == null ? "" : giveListParam.getMobile();
        String type = giveListParam.getType() == null ? "give" : giveListParam.getType();
        Integer pageNumber = giveListParam.getPage() == null ? Constants.PAGE_NUMBER : giveListParam.getPage();
        Integer pageSize = giveListParam.getPageSize() == null ? Constants.PAGE_SIZE : giveListParam.getPageSize();

        PaginationRequest paginationRequest = new PaginationRequest();
        Map<String, Object> searchParams = new HashMap<>();
        paginationRequest.setCurrentPage(pageNumber);
        paginationRequest.setPageSize(pageSize);

        if (type.equals("gived")) {
            searchParams.put("userId", mtUser.getId());
        } else {
            searchParams.put("giveUserId", mtUser.getId());
        }

        if (StringUtil.isNotEmpty(mobile) && type.equals("give")) {
            searchParams.put("mobile", mobile);
        } else if(StringUtil.isNotEmpty(mobile) && type.equals("gived")) {
            searchParams.put("userMobile", mobile);
        }
        paginationRequest.setSearchParams(searchParams);
        PaginationResponse<GiveDto> paginationResponse = giveService.queryGiveListByPagination(paginationRequest);

        Map<String, Object> outParams = new HashMap();
        outParams.put("content", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(outParams);
    }
}

