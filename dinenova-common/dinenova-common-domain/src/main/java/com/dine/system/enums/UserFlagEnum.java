package com.dine.system.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: processmanager
 * @description: UserFlagEnum
 * @author: Yjz
 * @create: 2021-07-27 13:53
 */
@Getter
@AllArgsConstructor
public enum UserFlagEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     * 内部账户
     */
    INSIDE(1, "内部账户"),
    /**
     * 管理员
     */
    ADMIN(2, "管理员"),
    /**
     * 普通用户
     */
    COMMON(3, "普通用户");

    /**
     * code码
     */
    @EnumValue
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static UserFlagEnum getEnumByCode(Integer code) {
        for (UserFlagEnum userFlagEnum : UserFlagEnum.values()) {
            if (userFlagEnum.getCode().equals(code)) {
                return userFlagEnum;
            }
        }
        return null;
    }

    public static UserFlagEnum getEnumByDesc(String desc) {
        for (UserFlagEnum userFlagEnum : UserFlagEnum.values()) {
            if (userFlagEnum.getDesc().equals(desc)) {
                return userFlagEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(UserFlagEnum.values()).map(UserFlagEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
