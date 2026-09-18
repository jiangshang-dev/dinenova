package com.dine.security;

import com.dine.constant.Constants;
import com.dine.utils.PropertiesUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 未登录时返回与原拦截器一致的 JSON
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        String message = PropertiesUtil.getResponseErrorMessageByCode(Constants.HTTP_RESPONSE_CODE_NOLOGIN);
        response.getWriter().print("{\"code\":1001,\"message\":\"" + message + "\",\"data\":null}");
    }
}
