package net.jueb.fuckGame.script.center.inner.game;

import java.util.Collection;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

public class GameUnRegScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameUnReg;
	}

	@Override
	public void action() {
		super.action();
		GameController game=(GameController) getParams().get(0);
		if(game!=null)
		{
			int serverId=game.getServerInfo().getServerId();
			GameManager.getInstance().remove(serverId);
			_log.info("游戏服务器移除,game="+game);
			Collection<Long> ids=RoleGameLockManager.getInstance().unlockByServer(serverId);//解锁角色
			broadcastRoleLockUpdate(serverId, LockServerType.Game, false, ids);
			notice_GameServerOffline(serverId);//通知网关断掉服务器连接
		}
	}
	
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		
	}
}
