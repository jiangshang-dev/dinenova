package com.dine.system.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: processmanager
 * @description: FileStatusEnum
 * @author: Yjz
 * @create: 2021-06-29 15:29
 */
@Getter
@AllArgsConstructor
public enum FileStatusEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     *
     */
    NOT_USED(1, "未使用"),
    /**
     *
     */
    USE(2, "使用中"),
    /**
     *
     */
    DELETE_FAIL(3, "删除失败");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static FileStatusEnum getEnumByCode(Integer code) {
        for (FileStatusEnum fileStatusEnum : FileStatusEnum.values()) {
            if (fileStatusEnum.getCode().equals(code)) {
                return fileStatusEnum;
            }
        }
        return null;
    }

    public static FileStatusEnum getEnumByDesc(String desc) {
        for (FileStatusEnum fileStatusEnum : FileStatusEnum.values()) {
            if (fileStatusEnum.getDesc().equals(desc)) {
                return fileStatusEnum;
            }
        }
        return null;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
