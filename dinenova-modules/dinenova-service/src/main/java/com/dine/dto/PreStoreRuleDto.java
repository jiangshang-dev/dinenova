package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 预存规则实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class PreStoreRuleDto implements Serializable {

    @Schema(description = "预存金额")
    private String preStoreAmount;

    @Schema(description = "目标金额")
    private String targetAmount;

}
