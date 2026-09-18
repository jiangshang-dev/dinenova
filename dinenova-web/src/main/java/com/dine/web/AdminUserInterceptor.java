package com.dine.web;

import com.dine.constant.Constants;
import com.dine.dto.AccountInfo;
import com.dine.util.AuthUserUtil;
import com.dine.util.TokenUtil;
import com.dine.utils.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 后台登录拦截
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class AdminUserInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("Access-Token");

        // 验证Token
        if (StringUtils.isEmpty(accessToken)) {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getOutputStream().print("{\"code\":1001,\"message\":\"" + PropertiesUtil
                    .getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN) + "\",\"data\":null}");
            return false;
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(accessToken);
        // 验证session中的Token
        if (accountInfo != null && accountInfo.getToken().equals(accessToken)) {
            AuthUserUtil.set(accountInfo);
            return true;
        }

        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getOutputStream().print("{\"code\":1001,\"message\":\"" + PropertiesUtil
                .getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN) + "\",\"data\":null}");
        return false;
    }
}
