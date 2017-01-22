package net.jueb.fuckGame.script.game.innerScript.gate;

import net.jueb.fuckGame.core.common.dto.RoleGateEventMsg;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.manager.RoleManager;
import net.jueb.fuckGame.script.game.innerScript.AbstractInnerScript;

/**
 * 网关角色事件
 * @author Administrator
 */
public class GateEventScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_RoleGateEvent;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		RoleGateEventMsg event=new RoleGateEventMsg();
		event.readFrom(clientBuffer);
		_log.debug("收到角色网关事件:"+event);
		long roleId=event.getRoleId();
		RoleController role=RoleManager.getInstance().getById(roleId);
		if(role==null)
		{
			_log.error("角色不存在,event="+event);
			return;
		}
		switch (event.getEvent()) {
		case Login://登录
		case ReConnect://重连
		{
			role.setGateServer(event.getServerId());
			role.setOnline(true);
		}
			break;
		case Disconnect:
		{
			role.setOnline(false);
		}
			break;
		case LogOut:
		{
			role.setGateServer(-1);
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void action() {
		
	}
}
