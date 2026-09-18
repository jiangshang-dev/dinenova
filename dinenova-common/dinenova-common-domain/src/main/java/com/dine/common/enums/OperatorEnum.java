package com.dine.common.enums;

import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: processmanager
 * @description: StatusEnum
 * @author: Yjz
 * @create: 2021-07-28 09:32
 */
@Getter
@AllArgsConstructor
public enum OperatorEnum implements IBaseEnum<Integer> {
    /**
     * 相等
     */
    EQ(1, "等于"),
    /**
     * 包含
     */
    CONTAIN(2, "包含"),
    /**
     * 大于
     */
    GT(3, "大于"),
    /**
     * 大于等于
     */
    GET(4, "大于等于"),
    /**
     * 小于
     */
    LT(5, "小于"),
    /**
     * 小于等于
     */
    LET(6, "小于等于");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    @Override
    public Integer getValue() {
        return code;
    }
}
