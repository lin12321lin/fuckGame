package net.jueb.fuckGame.script.center.inner;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.mybatis.MybatisUtil;
import net.jueb.fuckGame.center.mybatis.mapper.RoleMapper;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.common.dto.ServerResponse;
import net.jueb.fuckGame.core.common.dto.UserLoginInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.util4j.cache.callBack.CallBack;

/**
 * 同步角色数据到数据库
 * @author Administrator
 */
public class CreateRoleScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_CreateRole;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		UserLoginInfo user=(UserLoginInfo) getParams().get(0);
		CallBack<ServerResponse<Role>> call=(CallBack<ServerResponse<Role>>) getParams().get(1);
		GameErrCode code=GameErrCode.UnknownError;
		Role role=RoleCache.getInstance().getRoleByUid(user.getUid());
		if(role!=null)
		{
			code=GameErrCode.Succeed;
			call.call(new ServerResponse<Role>(code.value(),role));
			return ;
		}
		//创建角色
		Role newRole=buildNewRole(user);
		_log.debug("create new role:"+newRole);
		SqlSession sqlSession = null;
		try {
			sqlSession = MybatisUtil.getSqlSessionFactory().openSession(false);
			//创建角色
			RoleMapper mapper=sqlSession.getMapper(RoleMapper.class);
			role=mapper.findRoleByUid(user.getUid());
			if(role!=null & !StringUtils.isEmpty(user.getName()))
			{//如果之前有脚本已经创建了该角色
				_log.debug("create new role from db:"+newRole);
				role.setName(user.getName());
				role.setLastLogin(new Date());
				role.getConfig().setLastLoginIp(user.getIp());
				mapper.update(role);
				sqlSession.commit();
				RoleCache.getInstance().put(role);
				call.call(new ServerResponse<Role>(GameErrCode.Succeed.value(),role));
				return ;
			}
			mapper.insert(newRole);
			sqlSession.commit();
			buildRoleComplete(newRole);
			RoleCache.getInstance().put(newRole);
			role=newRole;
			code=GameErrCode.Succeed;
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
			if(sqlSession!=null)
			{
				sqlSession.rollback();
			}
		}finally{
			if(sqlSession!=null)
			{
				sqlSession.close();
			}
		}
		call.call(new ServerResponse<Role>(code.value(),role));
	}
	
	/**
	 * 创建一个角色模板
	 * @param uid
	 * @param pn
	 * @param name
	 * @return
	 */
	protected Role buildNewRole(UserLoginInfo user)
	{
		Role role=new Role();
		role.setUid(user.getUid());
		role.setSex(user.getSex());
		role.setName(user.getName());
		role.setPhone(user.getMobile());
		role.setFaceIcon(user.getFaceIcon());
		return role;
	}
	
	protected void buildRoleComplete(Role role)
	{
	
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer msg) {
		// TODO Auto-generated method stub
		
	}
}
