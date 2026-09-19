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
import java.nio.charset.StandardCharsets;

/**
 * 后台登录拦截
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class AdminUserInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String accessToken = request.getHeader("Access-Token");

        // 验证Token
        if (StringUtils.isEmpty(accessToken)) {
            writeNoLogin(response);
            return false;
        }

        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(accessToken);
        // 验证session中的Token
        if (accountInfo != null && accountInfo.getToken().equals(accessToken)) {
            AuthUserUtil.set(accountInfo);
            return true;
        }

        writeNoLogin(response);
        return false;
    }

    private void writeNoLogin(HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String message = PropertiesUtil.getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN);
        String body = "{\"code\":1001,\"message\":\"" + message + "\",\"data\":null}";
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }
}
