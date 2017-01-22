package net.jueb.fuckGame.script.gate.center;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;

public class GameRegScript extends AbstractCenterScript{

	@Override
	public void action() {
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		ServerInfo info=new ServerInfo();
		info.readFrom(clientBuffer);
		_log.info("大厅游戏服务器注册事件:"+info);
		NetConnection conn=GameConnectionManager.getInstance().getServerConnection(info.getServerId());
		if(conn==null || !conn.isActive())
		{
			_log.info("初始化游戏连接:"+info);
			GameConnectionManager.getInstance().initServerConnection(info);
		}
	}
}
