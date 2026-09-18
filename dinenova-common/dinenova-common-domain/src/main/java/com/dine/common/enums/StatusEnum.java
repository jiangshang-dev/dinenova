package com.dine.common.enums;

import com.dine.base.IBaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: processmanager
 * @description: StatusEnum
 * @author: Yjz
 * @create: 2021-07-28 09:32
 */
@Schema(description = "状态枚举", enumAsRef = true)
@Getter
@AllArgsConstructor
public enum StatusEnum implements IBaseEnum<Integer> {
    /**
     * 启用
     */
    @Schema(description = "启用") ENABLE(1, "启用"),
    /**
     * 禁用
     */
    @Schema(description = "禁用") DISABLE(2, "禁用");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static StatusEnum getEnumByCode(Integer code) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(StatusEnum.values()).map(StatusEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
