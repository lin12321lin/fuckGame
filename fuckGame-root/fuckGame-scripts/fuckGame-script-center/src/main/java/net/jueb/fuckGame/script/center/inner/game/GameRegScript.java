package net.jueb.fuckGame.script.center.inner.game;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.GateManager;
import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

public class GameRegScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		if(GameManager.getInstance().find(connection)!=null)
		{
			_log.warn("游戏重复发起注册请求:"+connection);
			response(connection,GameErrCode.Succeed);
			return;
		}
		GameErrCode code=GameErrCode.UnknownError;
		ServerInfo info=new ServerInfo();
		info.readFrom(buffer);
		try {
			if(GameManager.getInstance().has(info.getServerId()))
			{//因为id即使邀请码的右边2位,也是所有areaId共享的,所以唯一
				code=GameErrCode.RepeatServerIdRegError;
				_log.info("游戏服务器gb已经注册"+info+",无法重复注册");
				return ;
			}
			GameController game=GameManager.getInstance().regist(info, connection);
			code=GameErrCode.Succeed;
			notice_GameServerOnline(info);
			_log.info("游戏服务器注册"+game+",code="+code);
		} finally {
			response(connection, code);
		}
	}
	
	protected void response(NetConnection connection,GameErrCode code)
	{
		ByteBuffer buff=new ByteBuffer();
		buff.writeInt(code.value());
		GameMessage msg=new GameMessage(getMessageCode(),buff);
		connection.sendMessage(msg);
	}
	
	protected void notice_GameServerOnline(ServerInfo gameInfo)
	{
		ByteBuffer buffer=new ByteBuffer();
		gameInfo.writeTo(buffer);
		GateManager.getInstance().broadcast(new GameMessage(getMessageCode(),buffer));
		_log.info("广播网关服务器,游戏服务器"+gameInfo+"上线");
	}
}
