package net.jueb.fuckGame.script.center.inner.gate;

import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.center.ServerQueue;
import net.jueb.fuckGame.center.base.GateController;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.center.manager.GateManager;
import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 网关注册
 * @author Administrator
 */
public class GateRegScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GateReg;
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
		IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_GameConnectionUpdate, connection);
		ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, script);
	}
	
	private void response(NetConnection connection,GameErrCode code)
	{
		GameMessage msg=new GameMessage(getMessageCode());
		msg.getContent().writeInt(code.value());
		if(code==GameErrCode.Succeed)
		{
			
		}
		connection.sendMessage(msg);
	}
}
