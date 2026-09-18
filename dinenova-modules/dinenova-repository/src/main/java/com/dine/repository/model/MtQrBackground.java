package com.dine.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺二维码背景
 */
@Getter
@Setter
@TableName("mt_qr_background")
public class MtQrBackground implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer merchantId;

    private Integer storeId;

    private String name;

    private String filePath;

    private String status;

    private Date createTime;
}
