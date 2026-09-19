package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.ParamDto;
import com.dine.dto.StoreDto;
import com.dine.dto.UserInfo;
import com.dine.enums.StatusEnum;
import com.dine.service.*;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtMerchant;
import com.dine.repository.model.MtStore;
import com.dine.repository.model.MtTable;
import com.dine.repository.model.MtUser;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统接口相关controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-系统配置相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/system")
public class ClientSystemController extends BaseController {

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 商户接口
     */
    private MerchantService merchantService;

    /**
     * 桌码服务接口
     */
    private TableService tableService;

    /**
     * 获取系统配置
     *
     * @param request Request对象
     */
    @Operation(summary = "获取系统配置")
    @Debounce
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResponseObject config(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String platform = request.getHeader("platform");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        String storeId = request.getHeader("storeId") == null ? "" : request.getHeader("storeId");
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");
        String tableId =  request.getHeader("tableId") == null ? "" : request.getHeader("tableId");

        UserInfo loginInfo = TokenUtil.getUserInfoByToken(token);
        Integer merchantId = merchantService.getMerchantId(merchantNo);

        // 默认店铺，取会员之前选择的店铺
        MtStore storeInfo = null;
        MtUser mtUser = null;
        if (loginInfo != null) {
            mtUser = memberService.queryMemberById(loginInfo.getId());
            if (mtUser != null) {
                // 会员已禁用
                if (!mtUser.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    return getFailureResult(1001);
                }
                // 商户不同
                if (!mtUser.getMerchantId().equals(merchantId)) {
                    return getFailureResult(1001);
                }
            }
        }

        // 扫码下单
        if (StringUtil.isNotEmpty(tableId)) {
            MtTable mtTable = tableService.queryTableById(Integer.parseInt(tableId));
            if (mtTable != null && mtTable.getStoreId() != null) {
                storeId = mtTable.getStoreId().toString();
            }
        }

        // 默认的店铺
        if (StringUtil.isNotEmpty(storeId)) {
            storeInfo = storeService.queryStoreById(Integer.parseInt(storeId));
            // 店铺是否已关闭
            if (storeInfo != null) {
                if (!storeInfo.getStatus().equals(StatusEnum.ENABLED.getKey())) {
                    storeInfo = null;
                }
            }
        }

        // 取距离最近的
        if (storeInfo == null && StringUtil.isNotEmpty(latitude) && StringUtil.isNotEmpty(longitude)) {
            List<MtStore> storeList = storeService.queryByDistance(merchantNo, "", latitude, longitude);
            if (storeList.size() > 0) {
                storeInfo = storeList.get(0);
            }
        }

        // 最后取系统默认的店铺
        if (storeInfo == null) {
            storeInfo = storeService.getDefaultStore(merchantNo);
        }

        // 完善会员的店铺信息
        if (mtUser != null && (mtUser.getStoreId() == null || mtUser.getStoreId() < 1)) {
            mtUser.setStoreId(storeInfo.getId());
            mtUser.setUpdateTime(new Date());
            memberService.updateMember(mtUser, false);
        }

        StoreDto storeDto = new StoreDto();
        if (storeInfo != null) {
            BeanUtils.copyProperties(storeInfo, storeDto);
            MtMerchant mtMerchant = merchantService.queryMerchantById(storeInfo.getMerchantId());
            if (mtMerchant != null) {
                storeDto.setMerchantNo(mtMerchant.getNo());
            }
        } else {
            storeDto = null;
        }

        // 支付方式列表
        List<ParamDto> payTypeList = settingService.getPayTypeList(platform);

        Map<String, Object> result = new HashMap<>();
        result.put("storeInfo", storeDto);
        result.put("payTypeList", payTypeList);

        return getSuccessResult(result);
    }
}
