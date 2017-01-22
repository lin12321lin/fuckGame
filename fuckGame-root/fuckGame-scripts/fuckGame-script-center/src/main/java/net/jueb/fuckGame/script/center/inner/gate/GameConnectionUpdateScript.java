package net.jueb.fuckGame.script.center.inner.gate;

import java.util.Collection;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.GateManager;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

/**
 * 向所有网关更新游戏服务链接
 * @author Administrator
 */
public class GameConnectionUpdateScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_GameConnectionUpdate;
	}
	
	@Override
	public void action() {
		NetConnection conn=getParamOrNull(0);
		Collection<GameController> games=GameManager.getInstance().getList();
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(games.size());
		for(GameController g:games)
		{
			g.getServerInfo().writeTo(buffer);
		}
		GameMessage msg=new GameMessage(getMessageCode(),buffer);
		if(conn!=null)
		{
			conn.sendMessage(msg);
			_log.debug("通知网关更新游戏服链接,gateConn="+conn);
		}else
		{
			GateManager.getInstance().broadcast(msg);
			_log.debug("广播网关更新游戏服链接");
		}
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
	}
}
