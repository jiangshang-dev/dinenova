package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dine.dto.PageDto;
import com.dine.enums.PageEnum;
import com.dine.service.MtPageService;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtPage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页装修接口controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "小程序端-首页装修相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/diy")
public class ClientDiyPageController extends BaseController {

    private MtPageService pageService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @Operation(summary = "小程序首页装修展示")
    public ResponseObject index(HttpServletRequest request) {
        Integer storeId = resolveStoreId(request);

        PageDto dto = new PageDto();
        LambdaQueryWrapper<MtPage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MtPage::getStoreId, storeId);
        queryWrapper.eq(MtPage::getStatus, PageEnum.USE_ING.getCode());
        queryWrapper.orderByDesc(MtPage::getUpdateTime);
        List<MtPage> pageList = pageService.list(queryWrapper);
        if (pageList != null && !pageList.isEmpty()) {
            MtPage page = pageList.get(0);
            JSONObject pageDataJson = parsePageData(page.getPageData());
            JSONArray items = pageDataJson == null ? null : pageDataJson.getJSONArray("items");
            if (items != null && !items.isEmpty()) {
                BeanUtils.copyProperties(page, dto);
                dto.setPageDataJson(pageDataJson);
                dto.setItems(items);
                dto.setPage(pageDataJson.getJSONObject("page"));
                dto.setPageData("");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("page", dto);
        return getSuccessResult(result);
    }

    private JSONObject parsePageData(String pageData) {
        if (StringUtils.isBlank(pageData)) {
            return null;
        }
        try {
            return JSON.parseObject(pageData);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 优先用查询参数，其次用请求头，保证每个店铺读自己的装修
     */
    private Integer resolveStoreId(HttpServletRequest request) {
        String raw = request.getParameter("storeId");
        if (StringUtils.isBlank(raw)) {
            raw = request.getHeader("storeId");
        }
        if (StringUtils.isBlank(raw)) {
            return 0;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
