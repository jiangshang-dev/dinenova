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
 * @description: SecretEnum
 * @author: Yjz
 * @create: 2021-07-28 09:32
 */
@Getter
@AllArgsConstructor
public enum SecretEnum implements IEnum<Integer>, IBaseEnum<Integer> {
    /**
     * 启用
     */
    USERSECRET(1, "用户密级"),
    /**
     * 进行中
     */
    COMMENSECRET(2, "普通密级");

    /**
     * code码
     */
    private final Integer code;
    /**
     * 描述、备注、注释
     */
    private final String desc;

    public static SecretEnum getEnumByCode(Integer code) {
        for (SecretEnum statusEnum : SecretEnum.values()) {
            if (statusEnum.getCode().equals(code)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static SecretEnum getEnumByDesc(String desc) {
        for (SecretEnum statusEnum : SecretEnum.values()) {
            if (statusEnum.getDesc().equals(desc)) {
                return statusEnum;
            }
        }
        return null;
    }

    public static boolean contains(String name) {
        List<String> names = Arrays.stream(SecretEnum.values()).map(SecretEnum::name).collect(Collectors.toList());
        return names.contains(name);
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
