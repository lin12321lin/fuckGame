package net.jueb.fuckGame.script.center.inner;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;

/**
 * 同步角色数据到数据库
 * @author Administrator
 */
public class RoleFlushDbScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_RoleFlushDb;
	}

	@Override
	public void action() {
		RoleCache roleCache=RoleCache.getInstance();
		Set<Long> updates=roleCache.pollUpdates();
		_log.debug("开始刷新角色数据到数据库!count="+updates.size());
		if(!updates.isEmpty())
		{
			Set<Long> ids=updates;
			if(ids.isEmpty())
			{
				return ;
			}
			Queue<Role> roles=new LinkedBlockingQueue<Role>();
			for(long roleId:ids)
			{
				Role role=roleCache.getRoleById(roleId);
				roles.add(role);
			}
			List<Role> saveFailRoles=saveRoles(roles);
			for(Role role:saveFailRoles)
			{
				roleCache.updateMark(role.getId());
			}
		}
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer msg) {
		// TODO Auto-generated method stub
	}
}
