package net.jueb.fuckGame.script.center.inner.game;

import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

/**
 * 通知大厅解锁,如果所在当前服务器
 * @author juebanlin
 */
public class RoleUnlockIfLockScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameRoleUnlockIfLock;
	}
	
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		int serverId=buffer.readInt();
		long roleId=buffer.readLong();
		int locakServerId=RoleGameLockManager.getInstance().lockedServer(roleId);
		if(locakServerId==serverId)
		{//当前服务器
			RoleGameLockManager.getInstance().unLock(roleId);
			broadcastRoleLockUpdate(locakServerId, LockServerType.Game, false, roleId);
			_log.debug("玩家游戏解锁成功,roleId="+roleId+",serverId="+locakServerId);
		}else
		{//玩家锁定信息不是在当前服务器
			broadcastRoleLockUpdate(locakServerId, LockServerType.Game, true, roleId);//修正网关角色所在服务器信息
		}
	}
}
