package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.dine.repository.model.MtGoodsCate;
import com.dine.repository.model.MtGoodsSku;
import com.dine.repository.model.MtGoodsSpec;
import com.dine.repository.model.MtStore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品DTO
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
public class GoodsDto implements Serializable {

    @Schema(description = "自增ID")
    private Integer id;

    @Schema(description = "所属店铺ID")
    private Integer storeId;

    @Schema(description = "所属店铺信息")
    private MtStore storeInfo;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品类型")
    private String type;

    @Schema(description = "分类ID")
    private Integer cateId;

    @Schema(description = "分类信息")
    private MtGoodsCate cateInfo;

    @Schema(description = "商品条码")
    private String goodsNo;

    @Schema(description = "可否单规格")
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

    @Schema(description = "数量")
    private Integer num;

    @Schema(description = "服务时长")
    private Integer serviceTime;

    @Schema(description = "卡券ID")
    private String couponIds;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "最后操作人")
    private String operator;

    @Schema(description = "状态，A：正常；D：删除")
    private String status;

    @Schema(description = "skuId")
    private Integer skuId;

    @Schema(description = "sku列表")
    private List<MtGoodsSku> skuList;

    @Schema(description = "规格列表")
    private List<MtGoodsSpec> specList;

}

