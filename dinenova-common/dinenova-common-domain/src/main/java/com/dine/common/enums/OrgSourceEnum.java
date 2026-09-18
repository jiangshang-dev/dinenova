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
@Schema(description = "部门来源枚举", enumAsRef = true)
@Getter
@AllArgsConstructor
public enum OrgSourceEnum implements IBaseEnum<Integer> {
    /**
     * 启用
     */
    @Schema(description = "推送") ENABLE(1, "推送"),
    /**
     * 禁用
     */
    @Schema(description = "内置") DISABLE(2, "内置");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static OrgSourceEnum getEnumByCode(Integer code) {
        for (OrgSourceEnum statusEnum : OrgSourceEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(OrgSourceEnum.values()).map(OrgSourceEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
