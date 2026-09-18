package com.dine.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * diy首页装修实体
 * 
 * @Created by 袁腾飞老师
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("mt_page")
@Schema(name = "diy首页装修表对象", description = "diy首页装修表对象")
public class MtPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "page_id", type = IdType.AUTO)
    private Integer pageId;
    private Integer storeId;
    private String pageName;
    private String pageData;
    /**
     * 是否使用  0是  1否
     */
    private Integer status;
    private Integer isDelete;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;


}
