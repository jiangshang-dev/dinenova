package com.dine.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: airworthindess-db
 * @description: BaseModel
 * @author: Yjz
 * @create: 2022-10-19 10:52
 */
@Data
public class BaseModel implements Serializable {
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    protected Long createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    protected Long updateTime;

    /**
     * 创建用户
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    protected Integer createUser;

    /**
     * 更新用户
     */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    protected Integer updateUser;

    @TableField(exist = false)
    protected static final long serialVersionUID = 1L;
}
