package com.dine.base;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;

/**
 * 基础枚举类型
 *
 * @author java开发组
 */
public interface IBaseEnum<T extends Serializable> extends IEnum<T> {
    /**
     * 获取状态码
     *
     * @return int
     */
    T getCode();

    /**
     * 获取描述
     *
     * @return String
     */
    String getDesc();

    /**
     * 默认实现
     *
     * @return null
     */
    @Override
    default T getValue() {
        return null;
    }
}
