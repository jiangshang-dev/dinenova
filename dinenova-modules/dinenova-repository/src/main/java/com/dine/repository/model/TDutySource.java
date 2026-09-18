package com.dine.repository.model;

import io.swagger.v3.oas.annotations.media.Schema;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Getter
@Setter
@TableName("t_duty_source")
@Schema(name = "TDutySource对象", description = "")
public class TDutySource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "duty_source_id", type = IdType.AUTO)
    private Integer dutySourceId;

    private Integer dutyId;

    private Integer sourceId;


}
