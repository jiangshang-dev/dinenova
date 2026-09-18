package com.dine.repository.bean;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 店铺距离对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@Schema(name = "店铺距离对象", description = "店铺距离对象")
public class StoreDistanceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "店铺ID")
    private Integer id;

    @Schema(description = "数量")
    private String distance;

}
