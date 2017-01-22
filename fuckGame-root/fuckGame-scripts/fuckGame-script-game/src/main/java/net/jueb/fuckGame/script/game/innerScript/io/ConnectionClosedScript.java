package net.jueb.fuckGame.script.game.innerScript.io;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.game.ServerMain;
import net.jueb.fuckGame.game.ServerQueue;
import net.jueb.fuckGame.game.base.GateController;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.manager.GateManager;
import net.jueb.fuckGame.script.game.innerScript.AbstractInnerScript;


public class ConnectionClosedScript extends AbstractInnerScript{

	@Override
	public void action() {
		NetConnection connection=(NetConnection) getParams().get(0);
		GateController gate=GateManager.getInstance().find(connection);
		if(gate!=null)
		{
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Game_GateUnReg, gate);
			ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN,script);
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_ConnectionClosed;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}
}
