package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 商品规格子类实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsSpecChildDto implements Serializable {

   @Schema(description = "自增ID")
   private Integer id;

   @Schema(description = "规格名称")
   private String name;

   @Schema(description = "是否选择")
   private boolean checked;

}

