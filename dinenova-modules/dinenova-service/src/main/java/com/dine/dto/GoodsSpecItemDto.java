package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规格项实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSpecItemDto implements Serializable {

    @Schema(description = "自增ID")
    private Integer id;

    @Schema(description = "规格名称")
    private String name;

    @Schema(description = "规格子类")
    private List<GoodsSpecChildDto> child;

}

