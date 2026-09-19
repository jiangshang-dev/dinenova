package com.dine.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.dine.base.LoginUser;
import com.dine.debounce.DebounceCache;
import com.dine.debounce.annotation.Debounce;
import com.dine.exception.ServiceException;
import com.dine.utils.UserUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Lazy
@Aspect
@Component
public class ControllerDebounceAspect {
    @Resource
    private DebounceCache debounceCache;

    private final HttpServletRequest request;

    public ControllerDebounceAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before(value = "@annotation(anno)")
    public void before(Debounce anno) {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        long now = System.currentTimeMillis();

        //根据用户、URI、参数生成key
        String submitKey = genSubmitKey();

        //存在且未过期 → 重复提交
        Long expireTime = debounceCache.get(submitKey);
        if (expireTime != null) {
            if (now < expireTime) {
                throw new ServiceException("请勿重复提交，请稍后再试");
            } else {
                debounceCache.remove(submitKey);
            }
        }

        // 设置过期时间（注解上配置，默认2000ms）
        debounceCache.put(submitKey, now + anno.expire());
    }

    /**
     * 生成防重key
     *
     * @return md5 key
     */
    private String genSubmitKey() {
        StringJoiner raw = new StringJoiner("|");

        //用户标识
        String user = "anonymous";
        LoginUser loginUser = UserUtil.getUser();
        if (loginUser != null && loginUser.getId() != null) {
            user = loginUser.getId().toString();
        } else {
            String token = request.getHeader("Access-Token");
            if (StrUtil.isNotBlank(token)) {
                user = token;
            }
        }

        raw.add(user);
        raw.add(request.getRemoteAddr());
        raw.add(request.getRequestURI());
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            raw.add(queryString);
        }
        return DigestUtil.md5Hex(raw.toString());
    }
}
