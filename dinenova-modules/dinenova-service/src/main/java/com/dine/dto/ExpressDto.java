package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

/**
 * 订单物流信息dto
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ExpressDto {

    @Schema(description = "物流公司")
    private String expressCompany;

    @Schema(description = "物流单号")
    private String expressNo;

    @Schema(description = "发货时间")
    private String expressTime;

}
