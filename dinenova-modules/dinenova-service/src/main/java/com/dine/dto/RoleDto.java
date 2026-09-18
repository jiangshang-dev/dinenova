package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色信息实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class RoleDto {

    @Schema(description = "账户主键ID")
    private Long id;

    @Schema(description = "商户ID")
    private Integer merchantId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "角色类型")
    private String type;

    @Schema(description = "备注")
    private String description;

    @Schema(description = "状态 : A有效 D无效")
    private String status;

}
