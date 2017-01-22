package net.jueb.fuckGame.script.gate.publicScript.io;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.client.NetClient;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.base.ConnectionKey;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.factory.ScriptFactory;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;
import net.jueb.fuckGame.script.gate.publicScript.AbstractPublicScript;

/**
 * @author Administrator
 */
public class MessageArrivedScript extends AbstractPublicScript{

	@Override
	public void action() {
		NetConnection conn=(NetConnection) getParams().get(0);
		GameMessage msg=(GameMessage) getParams().get(1);
		if(!ServerMain.getInstance().isRegCenter())
		{
			sendGateError(conn,GameErrCode.HallServiceNotAvailable);
			_log.error("大厅服务不可用");
			return;
		}
		int code =msg.getCode();
		//系统内部消息范围1000-1999
		//大厅范围2000-3999;
		//游戏范围3000-4999;
		//网关消息范围4000-2999;
		int range=code/1000;
		IServerScript script=null;
		if(range==4)
		{//网关消息
			short queue =ServerQueue.MAIN;
			switch (code) {
			case GameMsgCode.Gate_UserLogin:
				queue =ServerQueue.Login;
				break;
			default:
				break;
			}
			script =ScriptFactory.getInstance().buildHandleRequest(code, conn,msg.getContent());
			if(script!=null)
			{
				ServerMain.getInstance().getQueues().execute(queue, script);
			}else
			{
				_log.error("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),script:"+script);
			}
			return;
		}
		RoleAgent ra=(RoleAgent) conn.getAttribute(ConnectionKey.RoleAgent);
		if(ra==null)
		{
			_log.error("unknown conn"+conn+",request code="+code);
			return ;
		}
		ByteBuffer content=msg.getContent();
		long roleId=ra.getRoleId();
		ra.setLastReadTime(System.currentTimeMillis());
		switch (range) {
		case 2://大厅消息范围
		{
			NetClient client=ServerMain.getInstance().getCenterClient();
			if(client!=null && client.isActive())
			{
				proxyRoleMsgToCenter(roleId, new GameMessage(code, content));
				_log.debug("Proxy Center RequestMessage[roleId="+roleId+",code="+code+"]");
			}else
			{
				sendGateError(ra.getConnection(),GameErrCode.HallServiceNotAvailable);
				String result="client.unActive()";
				_log.error("Proxy Center RequestMessage[roleId="+roleId+",code="+code+",result="+result+"]");
			}
		}
			break;
		case 3://游戏消息范围
		{
			int serverId=ra.getServerId();
			if(serverId<=0)
			{//角色为锁定服务器
				sendGateError(ra.getConnection(),GameErrCode.UnSupportOperation);
				String result="不在当前服务器";
				_log.error("Proxy Game("+serverId+") RequestMessage[roleId="+roleId+",code="+code+",result="+result+"]");
				return;
			}
			NetConnection gameConn=GameConnectionManager.getInstance().getServerConnection(serverId);
			if(gameConn!=null && gameConn.isActive())
			{
				proxyRoleMsgToConn(gameConn, roleId,  new GameMessage(code, content));
				_log.debug("Proxy Game("+serverId+") RequestMessage[roleId="+roleId+",code="+code+"]");
			}else
			{
				sendGateError(ra.getConnection(),GameErrCode.GameServiceNotAvailable);
				String result="conn unActive";
				_log.error("Proxy Game("+serverId+") RequestMessage[roleId="+roleId+",code="+code+",result="+result+"]");
			}
		}
			break;
		default://范围消息
			_log.error("error Range msg,code="+code);
			break;
		}
	}
	
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_MessageArrived;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		
	}
}
