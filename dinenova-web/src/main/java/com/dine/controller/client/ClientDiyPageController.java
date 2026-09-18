package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;
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

    @Debounce
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @Operation(summary = "小程序首页装修展示")
    public ResponseObject index(HttpServletRequest request) {
        Integer storeId = request.getParameter("storeId") == null ? 0 : Integer.parseInt(request.getParameter("storeId"));

        PageDto dto = new PageDto();
        LambdaQueryWrapper<MtPage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MtPage::getStoreId, storeId); // 查当前店铺的
        queryWrapper.eq(MtPage::getStatus, PageEnum.USE_ING.getCode()); // 默认正在使用的
        List<MtPage> pageList = pageService.list(queryWrapper);
        if (pageList != null && !pageList.isEmpty()) {
            MtPage page = pageList.get(0);
            BeanUtils.copyProperties(page, dto);

            dto.setPageDataJson(JSON.parseObject(page.getPageData()));
            dto.setItems((JSONArray) JSON.parseObject(page.getPageData()).get("items"));
            dto.setPage((JSONObject) JSON.parseObject(page.getPageData()).get("page"));
            dto.setPageData("");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("page", dto);
        return getSuccessResult(result);
    }

}
