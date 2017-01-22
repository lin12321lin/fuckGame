package net.jueb.fuckGame.script.center.inner.io;

import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.center.ServerQueue;
import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.base.GateController;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.GateManager;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;


public class ConnectionClosedScript extends AbstractInnerScript{

	@Override
	public void action() {
		NetConnection connection=getParam(0);
		_log.debug("链路关闭:"+connection+",args:"+connection.getAttributeNames());
		GateController gate=GateManager.getInstance().find(connection);
		if(gate!=null)
		{
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_GateUnReg, gate);
			ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN,script);
		}
		GameController game=GameManager.getInstance().find(connection);
		if(game!=null)
		{
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_GameUnReg, game);
			ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN,script);
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_ConnectionClosed;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}
}
