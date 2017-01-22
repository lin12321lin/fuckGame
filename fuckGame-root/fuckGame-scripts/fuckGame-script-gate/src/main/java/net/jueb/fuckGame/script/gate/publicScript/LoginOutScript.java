package net.jueb.fuckGame.script.gate.publicScript;

import net.jueb.fuckGame.core.common.enums.RoleGateEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.base.RoleAgent;

/**
 * 用户登出或者超时未登录
 * @author Administrator
 */
public class LoginOutScript extends AbstractPublicScript{

	@Override
	public void action() {
		RoleAgent ra=(RoleAgent) getParams().get(0);
		_log.debug("网关玩家登出:"+ra);
		roleGateEvent(ra,RoleGateEvent.LogOut);
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_LoginOut;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer msg) {
		
	}
}
