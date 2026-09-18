package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GroupDataListDto {

    @Schema(description = "键值")
    private String key;

    @Schema(description = "数据")
    private GroupDataDto data;

}
