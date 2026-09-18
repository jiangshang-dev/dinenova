package com.dine.debounce.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Debounce {
    /**
     * 防重过期时间，单位毫秒，默认2000ms（2s）
     */
    long expire() default 1000 * 2;
}
