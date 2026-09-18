package com.dine.security;

import com.dine.base.LoginUser;
import com.dine.dto.AccountInfo;
import com.dine.dto.UserInfo;
import com.dine.framework.exception.BusinessCheckException;
import com.dine.service.MemberService;
import com.dine.system.enums.UserFlagEnum;
import com.dine.util.AuthUserUtil;
import com.dine.util.TokenUtil;
import com.dine.utils.StringUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * 使用请求头 Access-Token 还原登录用户，并写入 Spring Security 上下文
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final MemberService memberService;

    public TokenAuthenticationFilter(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String token = request.getHeader("Access-Token");
            String uri = request.getRequestURI();
            if (StringUtil.isNotEmpty(token)) {
                if (uri.contains("/backendApi/") || uri.contains("/merchantApi/")) {
                    authenticateAccount(token);
                } else if (uri.contains("/clientApi/")) {
                    authenticateMember(token, uri);
                }
            }
            chain.doFilter(request, response);
        } finally {
            AuthUserUtil.clean();
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticateAccount(String token) {
        AccountInfo accountInfo = TokenUtil.getAccountInfoByToken(token);
        if (accountInfo == null || !token.equals(accountInfo.getToken())) {
            return;
        }
        AuthUserUtil.set(accountInfo);
        LoginUser loginUser = new LoginUser();
        loginUser.setId(accountInfo.getId());
        loginUser.setUserName(accountInfo.getAccountName());
        loginUser.setUserRealName(accountInfo.getRealName());
        loginUser.setToken(accountInfo.getToken());
        loginUser.setUserFlag(UserFlagEnum.ADMIN);
        if (StringUtil.isNotEmpty(accountInfo.getRoleIds())) {
            loginUser.setRoles(Arrays.asList(accountInfo.getRoleIds().split(",")));
        }
        setAuthentication(loginUser);
    }

    private void authenticateMember(String token, String uri) {
        UserInfo userInfo = TokenUtil.getUserInfoByToken(token);
        if (userInfo == null || StringUtil.isEmpty(userInfo.getToken()) || !token.equals(userInfo.getToken())) {
            return;
        }
        boolean active;
        try {
            active = Boolean.TRUE.equals(memberService.updateActiveTime(userInfo.getId()));
        } catch (BusinessCheckException e) {
            active = false;
        }
        if (!active && !uri.contains("/system/config")) {
            return;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userInfo.getId());
        loginUser.setToken(userInfo.getToken());
        loginUser.setUserFlag(UserFlagEnum.COMMON);
        setAuthentication(loginUser);
    }

    private void setAuthentication(LoginUser loginUser) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                loginUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
