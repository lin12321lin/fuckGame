package net.jueb.fuckGame.script.center.role;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.script.center.AbstractCenterBaseScript;

public abstract class AbstractRoleGameActionScript extends AbstractCenterBaseScript implements IRoleRoleGameActionScript {

	@Override
	public final void handleRequest(Request request) {
		handleAction(request.getConnection(), (RoleGameMessage) request.getContent());
	}
	
	protected abstract void handleAction(NetConnection connection,RoleGameMessage action);
	
	@Override
	public void action() {
		
	}
	
	/**
	 * 回复请求
	 * @param msg
	 */
	protected final void responseAction(long roleId,GameMessage msg)
	{
		RoleGameMessage rmsg=new RoleGameMessage(roleId, msg);
		proxyRoleMsgToGate(rmsg);
	}
	
	/**
	 * 回复请求
	 * @param msg
	 */
	protected final void responseAction(GameMessage msg)
	{
		RoleGameMessage req=(RoleGameMessage)getRequest().getContent();
		responseAction(req.getRoleId(), msg);
	}
}