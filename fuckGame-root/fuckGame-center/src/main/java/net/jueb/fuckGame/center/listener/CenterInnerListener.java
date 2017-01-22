package net.jueb.fuckGame.center.listener;

import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.center.ServerQueue;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.listener.GameServerConnectionListener;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;

public class CenterInnerListener extends GameServerConnectionListener {

	@Override
	public void messageArrived(NetConnection conn, GameMessage msg) {
		int code =msg.getCode();
		IServerScript script =ScriptFactory.getInstance().buildAction(GameMsgCode.Center_MessageArrived,conn,msg);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(ServerQueue.IO_EVENT, script);
		}else
		{
			_log.error("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),script:"+script);
		}
	}
	
	@Override
	public void connectionOpened(NetConnection connection) {
		_log.debug("链路打开:"+connection);
	}

	@Override
	public void connectionClosed(NetConnection connection) {
		IServerScript script =ScriptFactory.getInstance().buildAction(GameMsgCode.Center_ConnectionClosed,connection);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(ServerQueue.IO_EVENT, script);
		}
	}
}
