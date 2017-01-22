package net.jueb.fuckGame.script.game.innerScript.gate;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.base.GateController;
import net.jueb.fuckGame.game.manager.GateManager;
import net.jueb.fuckGame.script.game.innerScript.AbstractInnerScript;

/**
 * 网关注册
 * @author Administrator
 */
public class GateRegScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_GateReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		GameErrCode code=GameErrCode.UnknownError;
		ServerInfo info=new ServerInfo();
		info.readFrom(clientBuffer);
		_log.info("网关申请注册:"+info);
		if(GateManager.getInstance().find(connection)!=null)
		{
			_log.warn("网关重复发起注册请求:"+connection);
			response(connection,GameErrCode.Succeed);
			return;
		}
		GateController gate=GateManager.getInstance().get(info.getServerId());
		if(gate!=null)
		{
			code=GameErrCode.RepeatServerIdRegError;
			_log.error("网关注册失败,已存在相同ID网关:gate="+gate+",info="+info);
			response(connection,code);
			return;
		}
		GateManager.getInstance().regist(info,connection);
		response(connection,GameErrCode.Succeed);
		_log.info("网关注册成功:"+info);
	}
	
	private void response(NetConnection connection,GameErrCode code)
	{
		GameMessage msg=new GameMessage(getMessageCode());
		msg.getContent().writeInt(code.value());
		msg.getContent().writeInt(ServerConfig.SERVER_ID);
		connection.sendMessage(msg);
	}

	@Override
	public void action() {
		
	}
}
