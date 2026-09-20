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
 * 商品加料
 */
@Getter
@Setter
@TableName("mt_goods_feed")
@Schema(name = "MtGoodsFeed对象", description = "商品加料")
public class MtGoodsFeed implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "加料ID")
    @TableId(value = "feed_id", type = IdType.AUTO)
    private Integer feedId;

    @Schema(description = "商品ID")
    private Integer goodsId;

    @Schema(description = "加料名称")
    private String feedName;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
