package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 请求参数实体类
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class ParamDto implements Serializable {

    @Schema(description = "参数键值")
    private String key;

    @Schema(description = "参数名称")
    private String name;

    @Schema(description = "参数值")
    private String value;

}
