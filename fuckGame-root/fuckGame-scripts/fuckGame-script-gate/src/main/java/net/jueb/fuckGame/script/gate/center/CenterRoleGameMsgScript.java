package net.jueb.fuckGame.script.gate.center;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;
import net.jueb.fuckGame.script.gate.center.AbstractCenterScript;

/**
 * 处理服务器的代理消息并发往客户端
 * @author Administrator
 */
public class CenterRoleGameMsgScript extends AbstractCenterScript{

	@Override
	public void action() {
		
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_RoleGameMessage;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		RoleGameMessage pmsg=new RoleGameMessage();
		pmsg.readFrom(clientBuffer);
		long roleId=pmsg.getRoleId();
		GameMessage msg=pmsg.getMsg();
		RoleAgent ra=RoleAgentManager.getInstance().findAgent(roleId);
		if(ra!=null)
		{
			ra.sendMessage(msg);
			_log.debug("Proxy Center RoleGameMessage="+pmsg);
		}else
		{
			_log.error("Proxy Center RoleGameMessage="+pmsg+",RoleAgent not found"+"]");
		}
	}
}
