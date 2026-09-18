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
 * <p>
 * 开卡赠礼明细表
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_open_gift_item")
@Schema(name = "MtOpenGiftItem对象", description = "开卡赠礼明细表")
public class MtOpenGiftItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "自增ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "会用ID")
    private Integer userId;

    @Schema(description = "赠礼ID")
    private Integer openGiftId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "状态")
    private String status;


}
