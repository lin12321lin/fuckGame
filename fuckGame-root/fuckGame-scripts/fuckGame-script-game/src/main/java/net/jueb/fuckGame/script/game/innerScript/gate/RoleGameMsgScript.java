package net.jueb.fuckGame.script.game.innerScript.gate;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.manager.RoleManager;
import net.jueb.fuckGame.script.game.innerScript.AbstractInnerScript;

/**
 * 处理服务器的代理消息并发往客户端
 * @author Administrator
 */
public class RoleGameMsgScript extends AbstractInnerScript{

	//发送代理消息
	@Override
	public void action() {
		NetConnection conn=(NetConnection) getParams().get(0);
		RoleGameMessage msg=(RoleGameMessage) getParams().get(1);
		msg.setServerId(ServerConfig.SERVER_ID);
		conn.sendMessage(msg.wrapGameMessage(getMessageCode()));//包装到Gate_RoleGameMessage消息中
		_log.trace("Send Gate RoleGameMessage="+msg);
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_RoleGameMessage;
	}
	
	/**
	 *收到代理消息
	 */
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		RoleGameMessage rmsg=new RoleGameMessage();
		rmsg.readFrom(clientBuffer);
		long roleId=rmsg.getRoleId();
		GameMessage msg=rmsg.getMsg();
		int pcode=msg.getCode();
		RoleController role=RoleManager.getInstance().getById(roleId);
		if(role==null)
		{
			_log.error("角色不存在当前服务器,无法执行代理消息,Gate RoleGameMessage="+msg);
			sendGameErrorInfo(connection, roleId, GameErrCode.RoleNotFound);
			//通知大厅此玩家解锁
			noticeCenterUnlockIfLock(roleId);
			return ;
		}
		_log.trace("Revice Gate Gate RoleGameMessage="+msg);
		IServerScript script=ScriptFactory.getInstance().buildHandleRequest(pcode, connection,rmsg);
		if(script!=null)
		{
			script.run();
		}
		_log.trace("Handle Gate RoleGameMessage="+msg+",script="+script);
	}
}
