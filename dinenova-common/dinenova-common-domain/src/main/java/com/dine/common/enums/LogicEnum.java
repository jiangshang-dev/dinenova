package com.dine.common.enums;

import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicEnum implements IBaseEnum<Integer> {
    /**
     *
     */
    AND(1, "&&", "与"),
    /**
     *
     */
    OR(2, "||", "或");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 符号
     */
    private final String symbol;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }
}
