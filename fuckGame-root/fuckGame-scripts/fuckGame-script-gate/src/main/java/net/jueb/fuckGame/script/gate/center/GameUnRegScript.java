package net.jueb.fuckGame.script.gate.center;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;

public class GameUnRegScript extends AbstractCenterScript{

	@Override
	public void action() {
		
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameUnReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		ByteBuffer buffer=clientBuffer;
		int serverId=buffer.readInt();
		_log.info("游戏服务器离线:"+serverId);
		NetConnection conn=GameConnectionManager.getInstance().removeServerConnection(serverId);
		if(conn!=null && conn.isActive())
		{
			conn.close();
			_log.info("断开网关与游戏服务器的链接:"+serverId);
		}
	}
}
