package com.dine.typehandler;

import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MapTypeHandler implements TypeHandler<Map<String, Object>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Map<String, Object> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSONUtil.toJsonStr(parameter));
    }

    @Override
    public Map<String, Object> getResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        if (!JSONUtil.isTypeJSONObject(result)) {
            return new HashMap<>();
        }
        return JSONUtil.parseObj(result);
    }

    @Override
    public Map<String, Object> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        if (!JSONUtil.isTypeJSONObject(result)) {
            return new HashMap<>();
        }
        return JSONUtil.parseObj(result);
    }

    @Override
    public Map<String, Object> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        if (!JSONUtil.isTypeJSONObject(result)) {
            return new HashMap<>();
        }
        return JSONUtil.parseObj(result);
    }
}
