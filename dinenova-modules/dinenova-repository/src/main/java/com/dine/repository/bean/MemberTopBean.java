package com.dine.repository.bean;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员排行对象
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@Schema(name = "会员排行对象", description = "会员排行对象")
public class MemberTopBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    @Schema(description = "卡券ID")
    private Integer id;

    /**
     * 会员名称
     */
    @Schema(description = "卡券ID")
    private String name;

    /**
     * 会员号
     */
    @Schema(description = "卡券ID")
    private String userNo;

    /**
     * 消费金额
     */
    @Schema(description = "卡券ID")
    private BigDecimal amount;

    /**
     * 购买数量
     */
    private Integer num;

}
