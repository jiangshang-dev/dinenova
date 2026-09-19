package com.dine.controller.backend;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.dine.debounce.annotation.Debounce;

import com.dine.dto.AccountInfo;
import com.dine.enums.AdminRoleEnum;
import com.dine.service.*;
import com.dine.util.TokenUtil;
import com.dine.util.TreeUtil;
import com.dine.vo.RouterVo;
import com.dine.framework.annoation.OperationServiceLog;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.param.backend.LoginRequest;
import com.dine.constant.Constants;
import com.dine.framework.web.BaseController;
import com.dine.framework.web.ResponseObject;
import com.dine.vo.backend.LoginResponse;
import com.dine.repository.model.TAccount;
import com.dine.repository.model.TDuty;
import com.dine.repository.model.TSource;
import com.dine.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.dine.domain.TreeNode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 后台登录接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Tag(name = "管理端-后台登录相关接口")
@RestController
@AllArgsConstructor
@RequestMapping("/backendApi/login")
public class BackendLoginController extends BaseController {

    /**
     * 后台账号服务接口
     * */
    private AccountService accountService;

    /**
     * 后台菜单服务接口
     * */
    private SourceService sourceService;

    /**
     * 后台角色服务接口
     * */
    private DutyService dutyService;

    /**
     * 后台登录
     * */
    @Operation(summary = "后台登录")
    @Debounce
    @RequestMapping(value="/doLogin", method = RequestMethod.POST)
    public ResponseObject doLogin(HttpServletRequest request, @RequestBody LoginRequest loginRequest) throws BusinessCheckException {
        String userAgent = request.getHeader("user-agent");
        LoginResponse response = accountService.doLogin(loginRequest, userAgent);
        return getSuccessResult(response);
    }

    /**
     * 获取登录信息接口
     * */
    @Operation(summary = "获取登录信息")
    @Debounce
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    public ResponseObject getInfo(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "登录信息已失效，请重新登录");
        }
        TAccount tAccount = accountService.getAccountInfoById(accountInfo.getId());
        if (accountInfo == null || tAccount == null || !tAccount.getAccountStatus().toString().equals("1")) {
            return getFailureResult(Constants.HTTP_RESPONSE_CODE_NOLOGIN);
        }

        List<Long> roleIds = accountService.getRoleIdsByAccountId(accountInfo.getId());
        List<String> roles = new ArrayList<>();
        if (roleIds.size() > 0) {
            for (int i = 0; i < roleIds.size(); i++) {
                 TDuty role = dutyService.getRoleById(roleIds.get(i));
                 for (AdminRoleEnum item : AdminRoleEnum.values()) {
                      if (role.getDutyType().equals(item.getKey())) {
                          roles.add(item.getValue());
                      }
                 }
            }
        }

        List<TSource> sources = sourceService.getMenuListByUserId(accountInfo.getMerchantId(), accountInfo.getId());
        List<String> permissions = new ArrayList<>();
        if (sources.size() > 0) {
            for (TSource source : sources) {
                if (source.getPath() != null) {
                    String permission = source.getPath().replaceAll("/", ":");
                    permissions.add(permission);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();

        result.put("accountInfo", accountInfo);
        result.put("roles", roles);
        result.put("permissions", permissions);

        return getSuccessResult(result);
    }

    /**
     * 获取登录路由菜单接口
     *
     * @return
     */
    @Operation(summary = "获取登录路由菜单接口")
    @Debounce
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    public ResponseObject getRouters(HttpServletRequest request) throws BusinessCheckException {
        String token = request.getHeader("Access-Token");
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null) {
            return getFailureResult(401, "登录信息已失效，请重新登录");
        }

        List<TSource> sources = sourceService.getMenuListByUserId(accountInfo.getMerchantId(), accountInfo.getId());

        List<TreeNode> trees = new ArrayList<>();
        TreeNode treeNode;
        for (TSource tSource : sources) {
            treeNode = new TreeNode();
            treeNode.setName(tSource.getSourceName());
            treeNode.setEname(tSource.getEname());
            treeNode.setNewIcon(tSource.getNewIcon());
            treeNode.setPath(tSource.getPath());
            treeNode.setId(tSource.getSourceId());
            treeNode.setLevel(tSource.getSourceLevel());
            treeNode.setIsMenu(tSource.getIsMenu());
            treeNode.setSort((tSource.getSourceStyle() == null || StringUtil.isEmpty(tSource.getSourceStyle())) ? 0 : Integer.parseInt(tSource.getSourceStyle()));
            if (tSource.getParentId() != null) {
                treeNode.setPId(tSource.getParentId());
            }
            treeNode.setUrl(tSource.getSourceCode());
            treeNode.setIcon(tSource.getIcon());
            trees.add(treeNode);
        }

        List<TreeNode> treeNodes = TreeUtil.sourceTreeNodes(trees);
        List<RouterVo> routers = sourceService.buildMenus(treeNodes);

        return getSuccessResult(routers);
    }

    /**
     * 退出后台登录
     * */
    @Operation(summary = "退出后台登录")
    @Debounce
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @OperationServiceLog(description = "退出后台系统")
    public ResponseObject logout(HttpServletRequest request) {
        String token = request.getHeader("Access-Token");
        if (StringUtil.isEmpty(token)) {
            return getFailureResult(Constants.HTTP_RESPONSE_CODE_USER_NOT_EXIST);
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo != null) {
            TokenUtil.removeToken(token);
        }

        return getSuccessResult(true);
    }
}
