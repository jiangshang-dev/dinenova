package com.dine.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BooleanEnum implements IBaseEnum<Integer> {
    /**
     *
     */
    TRUE(1, true, "真"),
    /**
     *
     */
    FALSE(0, false, "假");

    /**
     * code码
     */
    @EnumValue
    private final Integer code;
    /**
     * bool值
     */
    private final boolean bool;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static BooleanEnum valueOf(boolean bool) {
        return bool ? BooleanEnum.TRUE : BooleanEnum.FALSE;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
