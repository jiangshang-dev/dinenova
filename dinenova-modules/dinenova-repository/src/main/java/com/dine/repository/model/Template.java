package com.dine.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板
 *
 * @Created by 袁腾飞老师
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_template")
@Schema(name = "mt_template表对象", description = "mt_template表对象")
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private int id;
    private int merchantId;
    private int storeId;
    private String color;
    private String templateValue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    private String operator;
    private String status;

    @TableField(exist = false)
    private String merchantName;
}
