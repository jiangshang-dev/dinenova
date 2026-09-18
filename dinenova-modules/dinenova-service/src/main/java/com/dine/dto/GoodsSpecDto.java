package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSpecDto implements Serializable {

    @Schema(description = "自增ID")
    private Integer specId;

    @Schema(description = "规格名称")
    private String name;

    @Schema(description = "规格值列表")
    private List<GoodsSpecValueDto> valueList;

}

