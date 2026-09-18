package com.dine.common.enums;

import com.dine.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: processmanager
 * @description: DragPossionEnum
 * @author: Yjz
 * @create: 2021-09-03 16:00
 */
@Getter
@AllArgsConstructor
public enum SortTypeEnum implements IBaseEnum<Integer> {
    /**
     *
     */
    ASC(1, "升序"),
    /**
     * 进行中
     */
    DESC(2, "降序");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static SortTypeEnum getEnumByCode(Integer code) {
        for (SortTypeEnum treeDragPositionEnum : SortTypeEnum.values()) {
            if (treeDragPositionEnum.getCode().equals(code)) {
                return treeDragPositionEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(SortTypeEnum.values()).map(SortTypeEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }
}
