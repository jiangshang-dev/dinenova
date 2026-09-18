package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 充值规则实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class RechargeRuleDto implements Serializable {

    @Schema(description = "充值金额")
    private String rechargeAmount;

    @Schema(description = "赠送金额")
    private String giveAmount;

}
