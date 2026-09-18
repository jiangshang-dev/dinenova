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
 * @description: FileTypeEnum
 * @author: Yjz
 * @create: 2021-06-28 17:40
 */
@Getter
@AllArgsConstructor
public enum NoticeTypeEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     * 普通通知
     */
    COMMON(1, "普通通知");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static NoticeTypeEnum getEnumByCode(Integer code) {
        for (NoticeTypeEnum fileTypeEnum : NoticeTypeEnum.values()) {
            if (fileTypeEnum.getCode().equals(code)) {
                return fileTypeEnum;
            }
        }
        return null;
    }

    public static NoticeTypeEnum getEnumByDesc(String desc) {
        for (NoticeTypeEnum fileTypeEnum : NoticeTypeEnum.values()) {
            if (fileTypeEnum.getDesc().equals(desc)) {
                return fileTypeEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(NoticeTypeEnum.values()).map(NoticeTypeEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}

