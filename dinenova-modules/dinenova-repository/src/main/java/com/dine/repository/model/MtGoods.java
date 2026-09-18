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
 * 商品表
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_goods")
@Schema(name = "MtGoods对象", description = "MtGoods对象")
public class MtGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "商户ID")
    private Integer merchantId;

    @Schema(description = "所属店铺ID")
    private Integer storeId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品类型")
    private String type;

    @Schema(description = "分类ID")
    private Integer cateId;

    @Schema(description = "商品编码")
    private String goodsNo;

    @Schema(description = "是否单规格")
    private String isSingleSpec;

    @Schema(description = "主图地址")
    private String logo;

    @Schema(description = "图片地址")
    private String images;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "划线价格")
    private BigDecimal linePrice;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "关联卡券")
    private String couponIds;

    @Schema(description = "服务时长")
    private Integer serviceTime;

    @Schema(description = "重量")
    private BigDecimal weight;

    @Schema(description = "初始销量")
    private Integer initSale;

    @Schema(description = "商品卖点")
    private String salePoint;

    @Schema(description = "可否使用积分抵扣")
    private String canUsePoint;

    @Schema(description = "会员是否有折扣")
    private String isMemberDiscount;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "最后操作人")
    private String operator;

    @Schema(description = "A：正常；D：删除")
    private String status;


}
