package net.jueb.fuckGame.center.base;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.jueb.fuckGame.core.common.dto.JsonConfig;

@MappedTypes(value = {JsonConfig.class})
@MappedJdbcTypes(includeNullJdbcType=true, value = { JdbcType.VARCHAR })
public class JsonConfigTypeHandler extends BaseTypeHandler<JsonConfig>{

	private Type type=new TypeToken<JsonConfig>(){}.getType();
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, JsonConfig parameter, JdbcType jdbcType)
			throws SQLException {
		Gson gson=new Gson();
		String json=gson.toJson(parameter,type);
		ps.setString(i,json);
	}

	@Override
	public JsonConfig getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String json=rs.getString(columnName);
		if(json!=null)
		{
			Gson gson=new Gson();
			return  gson.fromJson(json, type);
		}
		return new JsonConfig();
	}

	@Override
	public JsonConfig getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String json=rs.getString(columnIndex);
		if(json!=null)
		{
			Gson gson=new Gson();
			return  gson.fromJson(json,type);
		}
		return new JsonConfig();
	}

	@Override
	public JsonConfig getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String json=cs.getString(columnIndex);
		if(json!=null)
		{
			Gson gson=new Gson();
			return  gson.fromJson(json,type);
		}
		return new JsonConfig();
	}
}
