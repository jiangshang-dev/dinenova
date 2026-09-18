package com.dine.aspect;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author java开发组
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAspect {
    /**
     * 访问controller的开始时间
     */
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();


    /**
     * 定义请求日志切入点，其切入点表达式有多种匹配方式,这里是指定路径
     */
    @Pointcut("within(com.dine.controller..*) && @within(org.springframework.web.bind.annotation.RestController)")
    public void controller() {
    }

    @Before("controller()")
    public void doBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        String userAgentStr = request.getHeader("User-Agent");
        //获取请求头中的User-Agent
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        //打印请求的内容
        startTime.set(System.currentTimeMillis());
        log.info("请求开始时间：{}", DateUtil.date(startTime.get()).toString(DatePattern.NORM_DATETIME_PATTERN));
        log.info("请求地址 : {} {}", request.getMethod(), request.getRequestURL().toString());
        String param = Arrays.toString(joinPoint.getArgs());
        if (param.length() > 1000) {
            param = param.substring(0, 1000);
        }
        log.info("请求参数 : {}", param);
        log.info("访问IP : {}", request.getRemoteAddr());
        log.info("操作系统: {}/{}", userAgent.getOs().toString(), userAgent.getPlatform().toString());
        log.info("浏览器信息：{} {}/{} {}", userAgent.getBrowser().getName(), userAgent.getBrowser().getVersion(userAgentStr), userAgent.getEngine().getName(), userAgent.getEngineVersion());
    }

    /**
     * 返回通知：
     * 1. 在目标方法正常结束之后执行
     * 1. 在返回通知中补充请求日志信息，如返回时间，方法耗时，返回值，并且保存日志信息
     *
     * @param ret ret
     */
    @AfterReturning(returning = "ret", pointcut = "controller()")
    public void doAfterReturning(Object ret) {
        long endTime = System.currentTimeMillis();
        String returnResult = null != ret ? ret.toString() : "";
        //返回结果日志打印最大长度
        int returnResultLogMaxSize = 1000;
        if (returnResult.length() > returnResultLogMaxSize) {
            returnResult = returnResult.substring(0, 1000);
        }

        // 处理完请求，返回内容之后，打印日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("请求返回 : {}", returnResult);
        log.info("请求耗时：{}ms", (endTime - startTime.get()));
        log.info("请求地址 : {} {}", request.getMethod(), request.getRequestURL().toString());
        log.info("请求结束时间：{}", DateUtil.date(endTime).toString(DatePattern.NORM_DATETIME_PATTERN));
        startTime.remove();
    }

    /**
     * 获取客户端IP地址
     *
     * @param request 请求对象
     * @return 客户端IP地址
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

}