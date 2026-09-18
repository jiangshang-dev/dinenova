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
public enum PermissionTypeEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     * 模块
     */
    MODULE(1, "模块"),
    /**
     * 菜单
     */
    MENU(2, "菜单"),
    /**
     * 导航
     */
    NAVIGATION(3, "导航"),
    /**
     * 节点
     */
    NODE(4, "按钮节点");
    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static PermissionTypeEnum getEnumByCode(Integer code) {
        for (PermissionTypeEnum permissionTypeEnum : PermissionTypeEnum.values()) {
            if (permissionTypeEnum.getCode().equals(code)) {
                return permissionTypeEnum;
            }
        }
        return null;
    }

    public static PermissionTypeEnum getEnumByDesc(String desc) {
        for (PermissionTypeEnum permissionTypeEnum : PermissionTypeEnum.values()) {
            if (permissionTypeEnum.getDesc().equals(desc)) {
                return permissionTypeEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(PermissionTypeEnum.values()).map(PermissionTypeEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
