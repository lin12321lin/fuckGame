package net.jueb.fuckGame.script.center.inner.gate;

import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.manager.RoleGateLockManager;
import net.jueb.fuckGame.core.common.dto.RoleGateEventMsg;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

public class RoleGateEventScript extends AbstractInnerScript{

	@Override
	public void action() {
		
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_RoleGateEvent;
	}
	
	/**
	 *收到代理消息
	 */
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		RoleGateEventMsg msg=new RoleGateEventMsg();
		msg.readFrom(clientBuffer);
		long roleId=msg.getRoleId();
		if(RoleCache.getInstance().getRoleById(roleId)==null)
		{
			_log.error("Role Not found from Revice Gate RoleGateEventMsg="+msg);
			return ;
		}
		_log.debug("收到玩家网关事件:"+msg);
		switch (msg.getEvent()) {
		case Login://锁定角色
			RoleGateLockManager.getInstance().lock(roleId, msg.getServerId());
			broadcastRoleLockUpdate(msg.getServerId(), LockServerType.Gate, true, roleId);
			break;
		case LogOut:
			RoleGateLockManager.getInstance().unLock(roleId);
			broadcastRoleLockUpdate(msg.getServerId(), LockServerType.Gate, false, roleId);
			break;
		default:
			break;
		}
	}
}
