package com.dine.system.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: processmanager
 * @description: PermissionTypeEnum 权限类型枚举
 * @author: Yjz
 * @create: 2021-07-28 09:58
 */
@Getter
@AllArgsConstructor
public enum DataPermissionTypeEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     * 原材料库权限
     */
    MATERIAL(1, "原材料库权限"),
    /**
     * 知识库权限
     */
    KNOWLEDGE(2, "知识库权限");
    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static DataPermissionTypeEnum getEnumByCode(Integer code) {
        for (DataPermissionTypeEnum permissionTypeEnum : DataPermissionTypeEnum.values()) {
            if (permissionTypeEnum.getCode().equals(code)) {
                return permissionTypeEnum;
            }
        }
        return null;
    }

    public static DataPermissionTypeEnum getEnumByDesc(String desc) {
        for (DataPermissionTypeEnum permissionTypeEnum : DataPermissionTypeEnum.values()) {
            if (permissionTypeEnum.getDesc().equals(desc)) {
                return permissionTypeEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(DataPermissionTypeEnum.values()).map(DataPermissionTypeEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
