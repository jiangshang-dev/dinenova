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
import java.util.Collections;
import java.util.List;

/**
 * @author mrhum
 */
public abstract class BaseListTypeHandler<T> implements TypeHandler<List<T>> {

    private final Class<T> genericType;

    @SuppressWarnings({"unchecked"})
    public BaseListTypeHandler() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.genericType = (Class<T>) type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public List<T> getResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (!JSONUtil.isTypeJSONArray(result)) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(result, this.genericType);
    }

    @Override
    public List<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (!JSONUtil.isTypeJSONArray(result)) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(result, this.genericType);
    }

    @Override
    public List<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (!JSONUtil.isTypeJSONArray(result)) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(result, this.genericType);
    }
}
