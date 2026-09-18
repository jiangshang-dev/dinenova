package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

/**
 * 个人资产实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class AssetDto {

    @Schema(description = "次卡数量")
    private Integer timer;

    @Schema(description = "储值卡数量")
    private Integer prestore;

    @Schema(description = "优惠券数量")
    private Integer coupon;
}
