package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.service.GenCodeService;
import com.dine.util.CommonUtil;
import com.dine.util.TokenUtil;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.constant.Constants;
import com.dine.enums.StatusEnum;
import com.dine.framework.pagination.PaginationRequest;
import com.dine.framework.pagination.PaginationResponse;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.repository.model.TGenCode;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成管理类controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-代码生成相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/genCode")
public class BackendGenCodeController extends BaseController {

    /**
     * 生成代码服务接口
     */
    private GenCodeService genCodeService;

    /**
     * 代码生成列表
     *
     * @param request HttpServletRequest对象
     * @return 代码生成列表
     */
    @Operation(summary = "代码生成列表查询")
    @Debounce
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:index')")
    public ResponseObject list(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        Integer page = request.getParameter("page") == null ? Constants.PAGE_NUMBER : Integer.parseInt(request.getParameter("page"));
        Integer pageSize = request.getParameter("pageSize") == null ? Constants.PAGE_SIZE : Integer.parseInt(request.getParameter("pageSize"));
        String tableName = request.getParameter("tableName");
        String status = request.getParameter("status");

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        PaginationRequest paginationRequest = new PaginationRequest();
        paginationRequest.setCurrentPage(page);
        paginationRequest.setPageSize(pageSize);

        Map<String, Object> params = new HashMap<>();
        if (StringUtil.isNotEmpty(tableName)) {
            params.put("tableName", tableName);
        }
        if (StringUtil.isNotEmpty(status)) {
            params.put("status", status);
        }
        paginationRequest.setSearchParams(params);
        PaginationResponse<TGenCode> paginationResponse = genCodeService.queryGenCodeListByPagination(paginationRequest);

        Map<String, Object> result = new HashMap<>();
        result.put("dataList", paginationResponse);

        return getSuccessResult(result);
    }

    /**
     * 更新代码生成状态
     *
     * @return
     */
    @Operation(summary = "更新代码状态")
    @Debounce
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:add')")
    public ResponseObject updateStatus(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String status = params.get("status") != null ? params.get("status").toString() : StatusEnum.ENABLED.getKey();
        Integer id = params.get("id") == null ? 0 : Integer.parseInt(params.get("id").toString());

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TGenCode tGenCode = genCodeService.queryGenCodeById(id);
        if (tGenCode == null) {
            return getFailureResult(201);
        }
        tGenCode.setId(id);
        tGenCode.setStatus(status);
        genCodeService.updateGenCode(tGenCode);

        return getSuccessResult(true);
    }

    /**
     * 保存代码生成
     *
     * @param request HttpServletRequest对象
     * @return
     */
    @Operation(summary = "保存代码生成")
    @Debounce
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:add')")
    public ResponseObject saveHandler(HttpServletRequest request, @RequestBody Map<String, Object> params) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        String id = params.get("id") == null ? "" : params.get("id").toString();
        String status = params.get("status") == null ? "" : params.get("status").toString();
        String tableName = params.get("tableName") == null ? "" : params.get("tableName").toString();
        String moduleName = params.get("moduleName") == null ? "" : params.get("moduleName").toString();
        String tablePrefix = params.get("tablePrefix") == null ? "" : params.get("tablePrefix").toString();
        String author = params.get("author") == null ? "" : params.get("author").toString();
        String backendPath = params.get("backendPath") == null ? "" : params.get("backendPath").toString();

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TGenCode tGenCode = new TGenCode();
        tGenCode.setPkName("id");
        tGenCode.setStatus(status);
        tGenCode.setTableName(tableName);
        tGenCode.setModuleName(moduleName);
        tGenCode.setTablePrefix(tablePrefix);
        tGenCode.setAuthor(author);
        tGenCode.setBackendPath(backendPath);
        tGenCode.setServiceName(CommonUtil.firstLetterToUpperCase(tableName));
        tGenCode.setPackageName(tableName);
        if (StringUtil.isNotEmpty(id)) {
            tGenCode.setId(Integer.parseInt(id));
            genCodeService.updateGenCode(tGenCode);
        } else {
            genCodeService.addGenCode(tGenCode);
        }

        return getSuccessResult(true);
    }

    /**
     * 获取代码生成详情
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取代码生成详情")
    @Debounce
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:index')")
    public ResponseObject info(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TGenCode tGenCode = genCodeService.queryGenCodeById(id);

        Map<String, Object> result = new HashMap<>();
        result.put("tGenCode", tGenCode);

        return getSuccessResult(result);
    }

    /**
     * 生成代码
     *
     * @param request
     * @return
     */
    @Operation(summary = "生成代码")
    @Debounce
    @RequestMapping(value = "/gen/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('system:genCode:gen')")
    public ResponseObject gen(HttpServletRequest request, @PathVariable("id") Integer id) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(1001, "请先登录");
        }

        TGenCode tGenCode = genCodeService.queryGenCodeById(id);
        if (tGenCode == null) {
            return getFailureResult(201, "生成代码不存在");
        }

        genCodeService.generatorCode(tGenCode.getTableName());
        return getSuccessResult(true);
    }
}
