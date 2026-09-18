package com.dine.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.dine.base.LoginUser;
import com.dine.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 自动填充字段
 *
 * @author java开发组
 */
@Slf4j
@Component
public class FillMetaObjectHandler implements MetaObjectHandler {
    /**
     * 创建用户
     */
    public static final String CREATE_USER = "createUser";
    /**
     * 更新用户
     */
    public static final String UPDATE_USER = "updateUser";
    /**
     * 创建时间
     */
    public static final String CREATE_TIME = "createTime";
    /**
     * 更新时间
     */
    public static final String UPDATE_TIME = "updateTime";


    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser loginUser = UserUtil.getUser();
        Integer userId = loginUser == null ? -1 : loginUser.getId();
        long currentTimeMillis = System.currentTimeMillis();

        if (this.isFillField(CREATE_USER, metaObject)) {
            this.setFieldValByName(CREATE_USER, userId, metaObject);
        }

        if (this.isFillField(CREATE_TIME, metaObject)) {
            this.setFieldValByName(CREATE_TIME, currentTimeMillis, metaObject);
        }

        if (this.isFillField(UPDATE_USER, metaObject)) {
            this.setFieldValByName(UPDATE_USER, userId, metaObject);
        }

        if (this.isFillField(UPDATE_TIME, metaObject)) {
            this.setFieldValByName(UPDATE_TIME, currentTimeMillis, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = UserUtil.getUser();
        Integer userId = loginUser == null ? -1 : loginUser.getId();
        if (this.isFillField(UPDATE_USER, metaObject)) {
            this.setFieldValByName(UPDATE_USER, userId, metaObject);
        }

        if (this.isFillField(UPDATE_TIME, metaObject)) {
            this.setFieldValByName(UPDATE_TIME, System.currentTimeMillis(), metaObject);
        }
    }

    /**
     * 是否填充字段
     *
     * @param fieldName  字段名
     * @param metaObject 元对象
     */
    private boolean isFillField(String fieldName, MetaObject metaObject) {
        return this.getFieldValByName(fieldName, metaObject) == null;
    }
}
