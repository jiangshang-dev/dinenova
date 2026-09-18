package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 卡券分组数据DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GroupDataDto implements Serializable {

    @Schema(description = "发放数量")
    private Integer sendNum;

    @Schema(description = "未发放数量")
    private Integer unSendNum;

    @Schema(description = "使用数量")
    private Integer useNum;

    @Schema(description = "过期数量")
    private Integer expireNum;

    @Schema(description = "取消数量")
    private Integer cancelNum;
}
