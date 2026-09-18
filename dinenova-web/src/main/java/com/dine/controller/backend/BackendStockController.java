package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.AccountInfo;
import com.dine.dto.GoodsDto;
import com.dine.enums.StatusEnum;
import com.dine.service.*;
import com.dine.util.CommonUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.mapper.MtGoodsMapper;
import com.dine.repository.mapper.MtGoodsSkuMapper;
import com.dine.repository.model.*;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商品库存管理管理controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-商品库存管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/stock")
public class BackendStockController extends BaseController {

    private MtGoodsMapper mtGoodsMapper;

    private MtGoodsSkuMapper mtGoodsSkuMapper;

    /**
     * 商品分类服务接口
     */
    private StockService stockService;

    /**
     * 配置服务接口
     * */
    private SettingService settingService;

    /**
     * 后台账户服务接口
     */
    private AccountService accountService;

    /**
     * 店铺服务接口
     */
    private StoreService storeService;

    /**
     * 获取库存管理记录列表
     *
     * @param request
     * @return
     * @throws BusinessCheckException
     */
    @Operation(summary = "获取库存管理记录列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        String searchStoreId = request.getParameter("storeId");
        String type = request.getParameter("type");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TAccount account = accountService.getAccountInfoById(accountInfo.getId());
        Integer storeId = account.getStoreId() == null ? 0 : account.getStoreId();

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(description)) {
            params.put("description", description);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        if (StringUtil.isNotEmpty(type)) {
            params.put("type", type);
        }
        if (account.getMerchantId() != null && account.getMerchantId() > 0) {
            params.put("merchantId", account.getMerchantId());
        }
        if (StringUtil.isNotEmpty(searchStoreId)) {
            params.put("storeId", searchStoreId);
        }
        if (storeId > 0) {
            params.put("storeId", storeId);
        }

        paginationRequest.setSearchParams(params);
        PaginationResponse<MtStock> paginationResponse = stockService.queryStockListByPagination(paginationRequest);

        Map<String, Object> paramsStore = new HashMap<>();
        paramsStore.put("status", StatusEnum.ENABLED.getKey());
        if (storeId != null && storeId > 0) {
            paramsStore.put("storeId", storeId.toString());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            paramsStore.put("merchantId", accountInfo.getMerchantId());
        }
        List<MtStore> storeList = storeService.queryStoresByParams(paramsStore);
        String imagePath = settingService.getUploadBasePath();

        Map<String, Object> result = new HashMap<>();
        result.put("imagePath", imagePath);
        result.put("storeList", storeList);
        result.put("paginationResponse", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 删除库存管理记录状态
     *
     * @return
     */
    @Operation(summary = "删除库存管理记录状态")
    @Debounce
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject delete(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtStock mtStock = stockService.queryStockById(id.longValue());
        if (mtStock == null) {
            return getFailureResult(201, "该数据不存在");
        }

        String operator = accountInfo.getAccountName();
        stockService.delete(id, operator);

        return getSuccessResult(true);
    }

    /**
     * 保存库存管理记录
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存库存管理记录")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject save(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String type = params.get("type") == null ? "" : CommonUtil.replaceXSS(params.get("type").toString());
        String description = params.get("description") == null ? "" : CommonUtil.replaceXSS(params.get("description").toString());
        String status = params.get("status") == null ? StatusEnum.ENABLED.getKey() : params.get("status").toString();
        Integer storeId = (params.get("storeId") == null || StringUtil.isEmpty(params.get("storeId").toString())) ? 0 : Integer.parseInt(params.get("storeId").toString());
        List<LinkedHashMap> goodsList = (List) params.get("goodsList");

        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        Integer myStoreId = accountDto.getStoreId();
        if (myStoreId > 0) {
            storeId = myStoreId;
        }

        MtStock info = new MtStock();
        info.setMerchantId(accountDto.getMerchantId());
        info.setDescription(description);
        info.setStatus(status);
        info.setStoreId(storeId);
        info.setType(type);
        String operator = accountDto.getAccountName();
        info.setOperator(operator);
        stockService.addStock(info, goodsList);
        return getSuccessResult(true);
    }

    /**
     * 获取库存管理记录详情
     *
     * @param request
     * @return
     */
    @Operation(summary = "获取库存管理记录详情")
    @Debounce
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('stock:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountDto = TokenUtil.getAccountInfoByToken(token);
        if (accountDto == null) {
            return getFailureResult(1001, "请先登录");
        }

        MtStock mtStock = stockService.queryStockById(id.longValue());
        Map<String, Object> param = new HashMap<>();
        param.put("STOCK_ID", mtStock.getId());
        List<MtStockItem> stockItems = stockService.queryItemByParams(param);
        List<GoodsDto> goodsList = new ArrayList<>();
        if (stockItems != null && stockItems.size() > 0) {
            for (MtStockItem stockItem : stockItems) {
                 MtGoods mtGoods = mtGoodsMapper.selectById(stockItem.getGoodsId());
                 GoodsDto goodsDto = new GoodsDto();
                 goodsDto.setGoodsNo(mtGoods.getGoodsNo());
                 goodsDto.setName(mtGoods.getName());
                 goodsDto.setLogo(mtGoods.getLogo());
                 goodsDto.setNum(stockItem.getNum());
                 goodsDto.setStatus(mtGoods.getStatus());
                 if (stockItem.getSkuId() != null && stockItem.getSkuId() > 0) {
                     MtGoodsSku mtGoodsSku = mtGoodsSkuMapper.selectById(stockItem.getSkuId());
                     if (mtGoodsSku != null) {
                         goodsDto.setGoodsNo(mtGoodsSku.getSkuNo());
                         if (StringUtil.isNotEmpty(mtGoodsSku.getLogo())) {
                             goodsDto.setLogo(mtGoodsSku.getLogo());
                         }
                     }
                 }
                 goodsList.add(goodsDto);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("stockInfo", mtStock);
        result.put("goodsList", goodsList);

        return getSuccessResult(result);
    }
}
