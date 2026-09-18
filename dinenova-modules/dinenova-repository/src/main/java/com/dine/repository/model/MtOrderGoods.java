package com.dine.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单商品表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_order_goods")
@Schema(name = "MtOrderGoods对象", description = "订单商品表")
public class MtOrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "订单ID")
    private Integer orderId;

    @Schema(description = "商品ID")
    private Integer goodsId;

    @Schema(description = "skuID")
    private Integer skuId;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "优惠价")
    private BigDecimal discount;

    @Schema(description = "商品数量")
    private Integer num;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "A：正常；D：删除")
    private String status;


}
