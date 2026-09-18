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
 * 卡券商品表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_coupon_goods")
@Schema(name = "MtCouponGoods对象", description = "卡券商品表")
public class MtCouponGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "卡券ID")
    private Integer couponId;

    @Schema(description = "商品ID")
    private Integer goodsId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "状态")
    private String status;

}
