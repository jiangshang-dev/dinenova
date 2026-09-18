package com.dine.typehandler;

import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author mrhum
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {

    /**
     * 泛型
     */
    private final Class<T> genericType;

    public BaseTypeHandler() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.genericType = (Class<T>) type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (!JSONUtil.isTypeJSONObject(result)) {
            return null;
        }
        return JSONUtil.toBean(result, this.genericType);
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (!JSONUtil.isTypeJSONObject(result)) {
            return null;
        }
        return JSONUtil.toBean(result, this.genericType);
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (!JSONUtil.isTypeJSONObject(result)) {
            return null;
        }
        return JSONUtil.toBean(result, this.genericType);
    }
}
