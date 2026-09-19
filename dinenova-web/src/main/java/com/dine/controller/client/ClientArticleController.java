package com.dine.controller.client;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.constant.Constants;
import com.dine.dto.ArticleDto;
import com.dine.enums.StatusEnum;
import com.dine.param.ArticleDetailParam;
import com.dine.param.ArticleListParam;
import com.dine.service.ArticleService;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.mapper.MtArticleMapper;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "会员端-文章相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/article")
public class ClientArticleController extends BaseController {

    private MtArticleMapper articleMapper;

    /**
     * 文章服务接口
     * */
    private ArticleService articleService;

    /**
     * 获取文章列表
     */
    @Operation(summary ="获取文章列表", description ="获取文章列表")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public ResponseObject list(HttpServletRequest request,  @RequestBody ArticleListParam articleListParam) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String title = articleListParam.getTitle();
        Integer page = articleListParam.getPage() == null ? Constants.PAGE_NUMBER : articleListParam.getPage();
        Integer pageSize = articleListParam.getPageSize() == null ? Constants.PAGE_SIZE : articleListParam.getPageSize();
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        params.put("status", StatusEnum.ENABLED.getKey());
        if (StringUtil.isNotEmpty(title)) {
            params.put("title", title);
        }
        if (StringUtil.isNotEmpty(merchantNo)) {
            params.put("merchantNo", merchantNo);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<ArticleDto> paginationResponse = articleService.queryArticleListByPagination(paginationRequest);

        Map<String, Object> outParams = new HashMap();
        outParams.put("content", paginationResponse.getContent());
        outParams.put("pageSize", paginationResponse.getPageSize());
        outParams.put("pageNumber", paginationResponse.getCurrentPage());
        outParams.put("totalRow", paginationResponse.getTotalElements());
        outParams.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(outParams);
    }

    /**
     * 获取文章详情
     */
    @Operation(summary ="获取文章详情", description ="根据ID获取文章详情")
    @Debounce
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseObject detail(@RequestBody ArticleDetailParam articleDetailParam) throws BusinessCheckException, InvocationTargetException, IllegalAccessException {
        String articleIdStr = articleDetailParam.getArticleId() == null ? "" : articleDetailParam.getArticleId();
        Integer articleId = 0;
        if (StringUtil.isNotEmpty(articleIdStr)) {
            articleId = Integer.parseInt(articleIdStr);
        }

        // 更新阅读点击数
        ArticleDto mtArticle = articleService.getArticleDetail(articleId);
        if (mtArticle != null && mtArticle.getId() != null) {
            articleMapper.increaseClick(mtArticle.getId());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("articleInfo", mtArticle);

        return getSuccessResult(result);
    }
}
