package com.dine.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 记录登录日志。原切面未启用，保留切入点以便后续接回。
 *
 * @author java开发组
 */
@Aspect
@Component
public class ControllerLoginAspect {

    /**
     * 定义请求日志切入点，其切入点表达式有多种匹配方式,这里是指定路径
     */
    @Pointcut("execution(* com.dine.controller..*LoginController.doLogin(..))")
    public void controller() {
    }
}
