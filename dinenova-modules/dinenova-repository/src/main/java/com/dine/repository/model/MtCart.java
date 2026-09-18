package com.dine.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * 购物车
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_cart")
@Schema(name = "MtCart对象", description = "购物车对象")
public class MtCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "会员ID")
    private Integer userId;

    @Schema(description = "所属商户ID")
    private Integer merchantId;

    @Schema(description = "店铺ID")
    private Integer storeId;

    @Schema(description = "桌码ID")
    private Integer tableId;

    @Schema(description = "是否游客")
    private String isVisitor;

    @Schema(description = "挂单号")
    private String hangNo;

    @Schema(description = "skuID")
    private Integer skuId;

    @Schema(description = "商品ID")
    private Integer goodsId;

    @Schema(description = "数量")
    private Integer num;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "状态")
    private String status;
}
