package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.service.StoreService;
import com.dine.service.TableService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtStore;
import com.dine.repository.model.MtTable;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-店铺相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/store")
public class ClientStoreController extends BaseController {

    /**
     * 店铺服务接口
     * */
    private StoreService storeService;

    /**
     * 桌码服务接口
     */
    private TableService tableService;

    /**
     * 获取店铺列表（根据距离排序）
     *
     * @param request Request对象
     */
    @Operation(summary = "获取店铺列表（根据距离排序）")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        String keyword = param.get("keyword") == null ? "" : param.get("keyword").toString();
        String latitude = request.getHeader("latitude") == null ? "" : request.getHeader("latitude");
        String longitude = request.getHeader("longitude") == null ? "" : request.getHeader("longitude");
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        List<MtStore> storeList = storeService.queryByDistance(merchantNo, keyword, latitude, longitude);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("data", storeList);

        return getSuccessResult(outParams);
    }

    /**
     * 获取店铺详情
     *
     * @param request Request对象
     */
    @Operation(summary = "获取店铺详情")
    @Debounce
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject detail(HttpServletRequest request) throws BusinessCheckException {
        Integer storeId = request.getHeader("storeId") == null ? 0 : Integer.parseInt(request.getHeader("storeId"));
        Integer tableId = request.getHeader("tableId") == null ? 0 : Integer.parseInt(request.getHeader("tableId"));
        if (tableId > 0) {
            MtTable mtTable = tableService.queryTableById(tableId);
            if (mtTable != null) {
                storeId = mtTable.getStoreId();
            }
        }

        MtStore storeInfo = storeService.queryStoreById(storeId);

        Map<String, Object> outParams = new HashMap<>();
        outParams.put("storeInfo", storeInfo);

        return getSuccessResult(outParams);
    }
}
