package net.jueb.fuckGame.gate.listener;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.listener.GameServerConnectionListener;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.factory.ScriptFactory;

public class PublicListener extends GameServerConnectionListener {


	@Override
	public void messageArrived(NetConnection conn, GameMessage msg) {
		int code=msg.getCode();
		IServerScript script =ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_MessageArrived,conn,msg);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(ServerQueue.IO_EVENT, script);
		}
		_log.trace("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),script:"+script);
	}

	@Override
	public void connectionOpened(NetConnection connection) {
		ServerMain.getInstance().getPublicClients().add(connection);
		ServerMain.getInstance().getOnlineConnCount().incrementAndGet();
	}

	@Override
	public void connectionClosed(NetConnection connection) {
		ServerMain.getInstance().getPublicClients().remove(connection);
		ServerMain.getInstance().getOnlineConnCount().decrementAndGet();
		IServerScript script =ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_ConnectionClosed,connection);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(ServerQueue.IO_EVENT, script);
		}
	}
}
