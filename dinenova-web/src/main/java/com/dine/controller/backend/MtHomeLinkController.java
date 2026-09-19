package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dine.constant.Constants;
import com.dine.dto.AccountInfo;
import com.dine.service.MtHomeLinkService;
import com.dine.util.TokenUtil;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.repository.model.MtHomeLink;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端-首页跳转链接接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/link")
public class MtHomeLinkController extends BaseController  {

    private MtHomeLinkService homeLinkService;

    @GetMapping(value = "/list")
    @PreAuthorize("@pms.hasPermission('link:list')")
    public ResponseObject list() {
        LambdaQueryWrapper<MtHomeLink> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MtHomeLink::getStatus, "A");
        List<MtHomeLink> list = homeLinkService.list(queryWrapper);
        return getSuccessResult(list);
    }

    @Operation(summary = "查询首页跳转链接")
    @GetMapping(value = "/queryList")
    @PreAuthorize("@pms.hasPermission('link:list')")
    public ResponseObject queryHandler(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name") == null ? "" : request.getParameter("name");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(name)) {
            params.put("name", name);
        }
        paginationRequest.setSearchParams(params);

        PaginationResponse<MtHomeLink> paginationResponse = homeLinkService.queryHomeLinkListByPagination(paginationRequest);
        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        return getSuccessResult(result);
    }

    @GetMapping(value = "/info")
    @PreAuthorize("@pms.hasPermission('link:list')")
    public ResponseObject info(Integer id) {
        MtHomeLink mtHomeLink = homeLinkService.getById(id);
        return getSuccessResult(mtHomeLink);
    }

    @Debounce
    @PostMapping(value = "/save")
    @PreAuthorize("@pms.hasPermission('link:save')")
    public ResponseObject save(@RequestBody MtHomeLink mtHomeLink) {
        if (mtHomeLink.getId() == null) {
            mtHomeLink.setCreateTime(LocalDateTime.now());
        }
        mtHomeLink.setUpdateTime(LocalDateTime.now());
        mtHomeLink.setStatus("A");
        homeLinkService.saveOrUpdate(mtHomeLink);
        return getSuccessResult(mtHomeLink);
    }

    @Debounce
    @DeleteMapping(value = "/delete")
    @PreAuthorize("@pms.hasPermission('link:delete')")
    public ResponseObject delete(Integer id) {
        MtHomeLink mtHomeLink = homeLinkService.getById(id);
        mtHomeLink.setStatus("D");
        mtHomeLink.setUpdateTime(LocalDateTime.now());
        homeLinkService.updateById(mtHomeLink);
        return getSuccessResult(mtHomeLink);
    }
}
