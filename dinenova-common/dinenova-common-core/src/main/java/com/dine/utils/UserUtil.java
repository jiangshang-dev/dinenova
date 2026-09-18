package com.dine.utils;

import com.dine.base.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具
 */
@Slf4j
public class UserUtil {

    /**
     * 获取当前登录用户信息
     *
     * @return 返回登录用户，未登录时返回 null
     */
    public static LoginUser getUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser loginUser) {
                return loginUser;
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
