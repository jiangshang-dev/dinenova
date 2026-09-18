package com.dine.utils;

import cn.hutool.core.util.StrUtil;
import com.dine.base.LoginUser;
import com.dine.exception.ServiceException;
import com.dine.system.enums.UserFlagEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

/**
 * jwt生成工具
 *
 * @author wzx
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * 过期时间
     */
    private static Long expire;
    /**
     * 密钥
     */
    private static SecretKey secretKey;
    /**
     * 表头令牌键
     */
    private static String tokenKey;

    public JwtUtil(@Value("${dinenova.security.jwt-secret:dinenova-jwt-secret-key-32bytes!!}") String secret,
                   @Value("${dinenova.security.token-name:Access-Token}") String tokenName,
                   @Value("${dinenova.security.expire:604800000}") Long expireMillis) {
        JwtUtil.expire = expireMillis;
        JwtUtil.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        JwtUtil.tokenKey = tokenName;
    }

    /**
     * 根据登录用户生成token
     */
    public static String enToken(LoginUser user) {
        HashMap<String, Object> claims = new HashMap<>(4);
        claims.put("id", user.getId());
        claims.put("userName", user.getUserName());
        claims.put("userRealName", user.getUserRealName());
        claims.put("userFlag", user.getUserFlag() == null ? null : user.getUserFlag().name());
        long expMillis = System.currentTimeMillis() + JwtUtil.expire;
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .signWith(JwtUtil.secretKey)
                .expiration(new Date(expMillis))
                .compact();
    }

    /**
     * 从请求中解析token
     */
    public static LoginUser deToken(HttpServletRequest request) {
        String token = request.getHeader(JwtUtil.tokenKey);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(JwtUtil.tokenKey);
        }
        return JwtUtil.deToken(token);
    }

    /**
     * 验证token
     */
    public static LoginUser deToken(String token) {
        if (StrUtil.isBlank(token)) {
            throw new ServiceException("未登录");
        }
        try {
            Claims claims = Jwts.parser().verifyWith(JwtUtil.secretKey).build().parseSignedClaims(token).getPayload();
            LoginUser user = new LoginUser();
            user.setId(claims.get("id", Integer.class));
            user.setUserName(claims.get("userName", String.class));
            user.setUserRealName(claims.get("userRealName", String.class));
            String userFlag = claims.get("userFlag", String.class);
            if (StrUtil.isNotBlank(userFlag)) {
                user.setUserFlag(UserFlagEnum.valueOf(userFlag));
            }
            user.setExpire(claims.getExpiration());
            user.setToken(token);
            return user;
        } catch (MalformedJwtException e) {
            throw new ServiceException("令牌不合法");
        } catch (ExpiredJwtException e) {
            throw new ServiceException("登录已过期");
        }
    }
}
