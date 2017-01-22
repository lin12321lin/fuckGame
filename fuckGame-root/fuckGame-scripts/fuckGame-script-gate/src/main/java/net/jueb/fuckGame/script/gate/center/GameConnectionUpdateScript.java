package net.jueb.fuckGame.script.gate.center;

import java.util.ArrayList;
import java.util.List;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;

public class GameConnectionUpdateScript extends AbstractCenterScript{

	@Override
	public void action() {
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_GameConnectionUpdate;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		List<ServerInfo> games=new ArrayList<ServerInfo>();
		int size=clientBuffer.readInt();
		for(int i=0;i<size;i++)
		{
			ServerInfo g=new ServerInfo();
			g.readFrom(clientBuffer);
			games.add(g);
		}
		_log.debug("收到服务器同步信息:"+games+",当前链接表信息:"+GameConnectionManager.getInstance().info());
		for(ServerInfo g:games)
		{
			NetConnection conn=GameConnectionManager.getInstance().getServerConnection(g.getServerId());
			if(conn==null || !conn.isActive())
			{
				boolean result=GameConnectionManager.getInstance().initServerConnection(g);
				if(result)
				{
					_log.debug("初始化游戏连接成功:"+g+",result="+result);
				}else{
					_log.error("初始化游戏连接失败:"+g+",result="+result);	
				}
			}
		}
	}
}
