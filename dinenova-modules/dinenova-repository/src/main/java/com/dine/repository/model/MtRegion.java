package com.dine.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 省市区数据表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_region")
@Schema(name = "MtRegion对象", description = "省市区数据表")
public class MtRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "区划信息ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "区划名称")
    private String name;

    @Schema(description = "父级ID")
    private Integer pid;

    @Schema(description = "区划编码")
    private String code;

    @Schema(description = "层级(1省级 2市级 3区/县级)")
    private Integer level;


}
