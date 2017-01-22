package net.jueb.fuckGame.game.listener;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.listener.GameClientConnectionListener;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.game.ServerMain;
import net.jueb.fuckGame.game.ServerQueue;
import net.jueb.fuckGame.game.factory.ScriptFactory;

public class CenterClientListener extends GameClientConnectionListener {
	
	@Override
	public void messageArrived(NetConnection conn, GameMessage msg) {
		int code =msg.getCode();
		ByteBuffer byteBuffer = msg.getContent();
		IServerScript script=null;
		short queue =ServerQueue.MAIN;
		switch (code) {
		case GameMsgCode.Center_GameReg:
		case GameMsgCode.Center_GameUnReg:
			queue =ServerQueue.MAIN;
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
		_log.debug("大厅链路打开:"+connection);
		IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_GameReg, connection);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, script);
		}
	}

	@Override
	public void connectionClosed(NetConnection connection) {
		ServerMain.getInstance().setRegCenter(false);
	}
}
