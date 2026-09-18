package com.dine.param;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import java.io.Serializable;

/**
 * 充值请求参数
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Data
public class RechargeParam implements Serializable {

    @Schema(description ="充值金额", name="rechargeAmount")
    private String rechargeAmount;

    @Schema(description ="自定义充值金额", name="customAmount")
    private String customAmount;

}
