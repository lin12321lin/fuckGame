package net.jueb.fuckGame.gate.listener;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.listener.GameClientConnectionListener;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.base.ConnectionKey;
import net.jueb.fuckGame.gate.factory.ScriptFactory;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;

public class GameClientListener extends GameClientConnectionListener {


	public GameClientListener() {
		super();
	}
	
	@Override
	public void messageArrived(NetConnection conn, GameMessage msg) {
		int code =msg.getCode();
		ByteBuffer byteBuffer = msg.getContent();
		IServerScript script=null;
		short queue =ServerQueue.MAIN;
		switch (code) {
		case GameMsgCode.Game_GateReg:
			queue =ServerQueue.ServersConns;
			break;
		default:
			break;
		}
		script =ScriptFactory.getInstance().buildHandleRequest(code, conn,byteBuffer);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(queue, script);
		}else
		{
			_log.error("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),script:"+script);
		}
	}

	@Override
	public void connectionOpened(NetConnection connection) {
		IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Game_GateReg,connection);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(ServerQueue.ServersConns, script);
		}
	}

	@Override
	public void connectionClosed(NetConnection connection) {
		if(connection.hasAttribute(ConnectionKey.GameServerId))
		{
			int serverId=(int) connection.getAttribute(ConnectionKey.GameServerId);
			GameConnectionManager.getInstance().removeServerConnection(serverId);
		}
	}
}
