package net.jueb.fuckGame.script.center.inner.game;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

/**
 * 玩家离开游戏服
 * @author juebanlin
 */
public class RoleExitScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameRoleExit;
	}
	
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		int serverId=buffer.readInt();
		long roleId=buffer.readLong();
		int locakServerId=RoleGameLockManager.getInstance().lockedServer(roleId);
		if(locakServerId<=0)
		{
			_log.error("玩家退出游戏错误,玩家不在任何游戏服务器,roleId="+roleId+",serverId="+serverId+",connection="+connection);
			broadcastRoleLockUpdate(locakServerId, LockServerType.Game, false, roleId);
		}else
		{
			GameController g=GameManager.getInstance().getById(serverId);
			if(g!=null)
			{
				if(locakServerId==serverId)
				{//当前服务器
					RoleGameLockManager.getInstance().unLock(roleId);
					broadcastRoleLockUpdate(locakServerId, LockServerType.Game, false, roleId);
					_log.debug("玩家退出游戏成功,roleId="+roleId+",serverId="+locakServerId);
				}else
				{//玩家锁定信息不是在当前服务器
					broadcastRoleLockUpdate(locakServerId, LockServerType.Game, true, roleId);//修正网关角色所在服务器信息
					_log.error("玩家退出游戏错误,服务器信息不一致,roleId="+roleId+",locakServerId="+locakServerId+",reqServer="+g.getServerInfo());
				}
			}else
			{
				broadcastRoleLockUpdate(locakServerId, LockServerType.Game, true, roleId);//修正网关角色所在服务器信息
				_log.error("玩家退出游戏错误,服务器不存在,roleId="+roleId+",serverId="+serverId+",connection="+connection);
			}
		}
	}
	
//	void response(NetConnection connection,int code)
//	{
//		ByteBuffer buff=new ByteBuffer();
//		buff.writeUTF(callKey);
//		buff.writeInt(code);
//		buff.writeLong(roleId);
//		connection.sendMessage(new GameMessage(getMessageCode(),buff));
//	}
}
