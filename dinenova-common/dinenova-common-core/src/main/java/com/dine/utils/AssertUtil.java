package com.dine.utils;

import cn.hutool.core.util.StrUtil;
import com.dine.exception.ServiceException;
import com.dine.base.IBaseEnum;


/**
 * 流程引擎断言工具
 *
 * @author java开发组
 */
public final class AssertUtil {
    public static void isTrue(boolean expression, IBaseEnum<Integer> base, Object... params) {
        if (!expression) {
            throw new ServiceException(base, params);
        }
    }

    public static void isTrue(boolean expression, String message, Object... params) {
        if (!expression) {
            throw new ServiceException(message, params);
        }
    }

    public static void notTrue(boolean expression, IBaseEnum<Integer> base, Object... params) {
        isTrue(!expression, base, params);
    }

    public static void notTrue(boolean expression, String message, Object... params) {
        isTrue(!expression, message, params);
    }

    public static void notNull(Object coll, IBaseEnum<Integer> base, Object... params) {
        isTrue(coll != null, base, params);
    }

    public static void notNull(Object coll, String message, Object... params) {
        isTrue(coll != null, message, params);
    }

    public static void isBlank(String content, IBaseEnum<Integer> base, Object... params) {
        isTrue(StrUtil.isBlank(content), base, params);
    }

    public static void isBlank(String content, String message, Object... params) {
        isTrue(StrUtil.isBlank(content), message, params);
    }

    public static void isNotBlank(String content, IBaseEnum<Integer> base, Object... params) {
        if (StrUtil.isBlank(content)) {
            throw new ServiceException(base, params);
        }
    }

    public static void isNotBlank(String content, String message, Object... params) {
        if (StrUtil.isBlank(content)) {
            throw new ServiceException(message, params);
        }
    }

    public static void exception(IBaseEnum<Integer> base, Object... params) {
        notTrue(true, base, params);
    }

    public static void exception(String message, Object... params) {
        notTrue(true, message, params);
    }
}
