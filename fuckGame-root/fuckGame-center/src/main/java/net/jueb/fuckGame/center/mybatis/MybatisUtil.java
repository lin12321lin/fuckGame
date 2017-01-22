package net.jueb.fuckGame.center.mybatis;

import java.io.InputStream;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;

import net.jueb.fuckGame.center.ServerConfig;
import net.jueb.fuckGame.core.common.enums.RoleSexEnum;
import net.jueb.fuckGame.core.common.enums.RoleStatus;
import net.jueb.fuckGame.core.util.Log4jUtil;

public class MybatisUtil {

	protected final static Logger _log = Log4jUtil.getLogger(MybatisUtil.class);

	private final static SqlSessionFactory sqlSessionFactory;
	static {
		InputStream in=MybatisUtil.class.getResourceAsStream("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(in,ServerConfig.SERVER_SETTINGS);
		//配置类型映射handler
		Configuration cfg = sqlSessionFactory.getConfiguration();
		TypeHandlerRegistry tr = cfg.getTypeHandlerRegistry();
		tr.register(new DateTypeHandler());
		tr.register(new EnumTypeHandler<RoleSexEnum>(RoleSexEnum.class));
		tr.register(new EnumTypeHandler<RoleStatus>(RoleStatus.class));
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}
