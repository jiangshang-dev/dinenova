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
public enum NoticeStatusEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     * 未读
     */
    UNREAD(1, "未读"),
    /**
     * 已读
     */
    READ(2, "已读"),
    /**
     * 拒绝
     */
    REJECT(3, "拒绝"),
    /**
     * 接受
     */
    ACCEPT(4, "接受");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static NoticeStatusEnum getEnumByCode(Integer code) {
        for (NoticeStatusEnum fileTypeEnum : NoticeStatusEnum.values()) {
            if (fileTypeEnum.getCode().equals(code)) {
                return fileTypeEnum;
            }
        }
        return null;
    }

    public static NoticeStatusEnum getEnumByDesc(String desc) {
        for (NoticeStatusEnum fileTypeEnum : NoticeStatusEnum.values()) {
            if (fileTypeEnum.getDesc().equals(desc)) {
                return fileTypeEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(NoticeStatusEnum.values()).map(NoticeStatusEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}

