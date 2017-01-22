package net.jueb.fuckGame.script.center.inner.game;

import java.util.Map.Entry;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.common.dto.RoleChange;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

/**
 * 玩家角色数据更新
 * @author Administrator
 */
public class RoleDataUpdateScript extends AbstractInnerScript{

	@Override
	public void action() {
		
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoleDataUpdate;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		long roleId=buffer.readLong();
		RoleChange change=new RoleChange();
		change.readFrom(buffer);
		Role role=RoleCache.getInstance().getRoleById(roleId);
		if(role==null)
		{
			_log.error("更新角色信息失败,role not found,conn="+connection+",change:"+change);
			return;
		}
		GameController gc=GameManager.getInstance().find(connection);
		if(gc!=null)
		{
			int lockedServerId=RoleGameLockManager.getInstance().lockedServer(role.getId());
			if(lockedServerId!=gc.getServerInfo().getServerId())
			{
				_log.error("更新角色信息失败,锁定服务器和当前消息通道服务器不一致,lockedServerId="+lockedServerId+",reqServer="+gc.getServerInfo()+",change:"+change);
				return;
			}
		}
		try {
			_log.debug("角色应用增量:role="+role+"\n"+change);
			applyChange(role, change);
			_log.debug("角色应用增量成功:role="+role+"\n"+change);
			RoleCache.getInstance().updateMark(role.getId());
		} catch (Exception e) {
			_log.debug("角色应用增量失败:role="+role+"\n"+change);
		}
	}
	
	
	/**
	 * 为角色应用增量
	 * @param role
	 * @param change
	 */
	protected void applyChange(Role role,RoleChange change)
	{
		long newMoney=role.getMoney()+change.getMoney();
		if(newMoney<0)
		{
			newMoney=0;
		}
		role.setMoney(newMoney);
		for(Entry<Integer, Integer> e:change.getItems().entrySet())
		{
			role.getConfig()._bagItemAdd(e.getKey(), e.getValue());
		}
	}
}
