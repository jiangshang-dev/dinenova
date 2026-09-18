package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

/**
 * 店铺实体
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 * */
@Getter
@Setter
public class StoreDto implements Serializable {

    @Schema(description = "自增ID")
    private Integer id;

    @Schema(description = "商户ID")
    private Integer merchantId;

    @Schema(description = "商户号")
    private String merchantNo;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "店铺名称")
    private String name;

    @Schema(description = "店铺二维码")
    private String qrCode;

    @Schema(description = "店铺LOGO")
    private String logo;

    @Schema(description = "是否默认店铺")
    private String isDefault;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "店铺地址")
    private String address;

    @Schema(description = "营业时间")
    private String hours;

    @Schema(description = "经度")
    private String latitude;

    @Schema(description = "纬度")
    private String longitude;

    @Schema(description = "备注信息")
    private String description;

    @Schema(description = "微信商户号")
    private String wxMchId;

    @Schema(description = "微信支付秘钥")
    private String wxApiV2;

    @Schema(description = "微信支付证书")
    private String wxCertPath;

    @Schema(description = "支付宝appId")
    private String alipayAppId;

    @Schema(description = "支付宝应用私钥")
    private String alipayPrivateKey;

    @Schema(description = "支付宝支付公钥")
    private String alipayPublicKey;

    @Schema(description = "营业执照")
    private String license;

    @Schema(description = "统一社会信用代码")
    private String creditCode;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行卡账户名")
    private String bankCardName;

    @Schema(description = "银行卡卡号")
    private String bankCardNo;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "状态，1：正常；2：删除")
    private String status;

    @Schema(description = "最后操作人")
    private String operator;

}