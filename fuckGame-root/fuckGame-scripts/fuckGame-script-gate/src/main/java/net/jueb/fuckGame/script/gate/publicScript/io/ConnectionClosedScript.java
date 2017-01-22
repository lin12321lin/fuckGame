package net.jueb.fuckGame.script.gate.publicScript.io;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.base.ConnectionKey;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.factory.ScriptFactory;
import net.jueb.fuckGame.script.gate.publicScript.AbstractPublicScript;


public class ConnectionClosedScript extends AbstractPublicScript{

	@Override
	public void action() {
		NetConnection connection=(NetConnection) getParams().get(0);
		_log.debug("connectionClosed:"+connection);
		if(connection.hasAttribute(ConnectionKey.RoleAgent))
		{//玩家掉线
			RoleAgent ra=(RoleAgent) connection.getAttribute(ConnectionKey.RoleAgent);
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_RoleDisConnect,ra);
			ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, script);
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_ConnectionClosed;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}
}
