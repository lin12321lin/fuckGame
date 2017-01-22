package net.jueb.fuckGame.script.center.role;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;

public class RefreshRoleDataScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_RoleHallDataRefresh;
	}

	/**
	 * 主动刷新,通知玩家弹回大厅并更新数据
	 */
	public void action() {
		long roleId=(long) getParams().get(0);
		roleHallDataRefresh(roleId);
	};
	
	RoleGameMessage action;
	@Override
	protected void handleAction(NetConnection connection, RoleGameMessage action) {
		this.action=action;
		roleHallDataRefresh(action.getRoleId());
	}
	
	protected void roleHallDataRefresh(long roleId)
	{
		Role role=RoleCache.getInstance().getRoleById(roleId);
		if(role==null)
		{
			return;
		}
		int lockedServerId=RoleGameLockManager.getInstance().lockedServer(role.getId());//当前玩家所在服务器
		boolean inGameServer= lockedServerId>0;
		//刷新大厅的时候,顺便更新网关的锁
		broadcastRoleLockUpdate(lockedServerId, LockServerType.Game,inGameServer, role.getId());
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeBoolean(inGameServer);//是否在游戏中
		buffer.writeUTF(role.getName());
		buffer.writeLong(role.getMoney());
		buffer.writeUTF(role.getFaceIcon());
		Map<Integer,Integer> item=new HashMap<>();
		for(Entry<Integer,Integer> e:role.getConfig().getBagItem().entrySet())
		{
			item.put(e.getKey(), e.getValue());
		}
		buffer.writeInt(item.size());
		for(Entry<Integer,Integer> e:item.entrySet())
		{
			buffer.writeInt(e.getKey());
			buffer.writeInt(e.getValue());
		}
		responseAction(new GameMessage(getMessageCode(), buffer));
	}
}