package net.jueb.fuckGame.script.gate.center;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;
import net.jueb.util4j.buffer.BytesBuff;

public class GateBroadcastScript extends AbstractCenterScript{

	@Override
	public void action() {
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_Broadcast;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		int code=clientBuffer.readInt();
		int dataSize=clientBuffer.readInt();
		BytesBuff data=clientBuffer.readBytes(dataSize);
		GameMessage msg=new GameMessage(code,new ByteBuffer(data.getBytes()));
		RoleAgentManager.getInstance().broadcast(msg);
		_log.debug("广播消息：code="+code);
	}
}
