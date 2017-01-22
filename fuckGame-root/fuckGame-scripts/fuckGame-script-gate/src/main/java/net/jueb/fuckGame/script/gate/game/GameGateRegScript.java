package net.jueb.fuckGame.script.gate.game;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.common.dto.Address;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.ServerConfig;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;

public class GameGateRegScript extends AbstractGameScript{

	@Override
	public void action() {
		ServerInfo info=new ServerInfo();
		info.setAddress(new Address(ServerConfig.LAN_HOST, ServerConfig.LAN_PORT));
		info.setServerId(ServerConfig.SERVER_ID);
		ByteBuffer buffer=new ByteBuffer();
		info.writeTo(buffer);
		GameMessage message=new GameMessage(getMessageCode(),buffer);
		NetConnection conn=(NetConnection) getParams().get(0);
		conn.sendMessage(message);
		_log.debug("请求注册到游戏服,conn="+conn);
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_GateReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		ByteBuffer buffer=clientBuffer;
		int code=buffer.readInt();
		int serverId=buffer.readInt();
		if(code==GameErrCode.Succeed.value())
		{
			NetConnection old=GameConnectionManager.getInstance().getServerConnection(serverId);
			if(old!=null && old.isActive())
			{
				_log.warn("已存在服务器链接"+serverId+",关闭当前连接:"+connection);
				connection.close();
			}else
			{
				GameConnectionManager.getInstance().addServerConnection(serverId,connection);
				_log.info("网关注册到游戏服务器"+serverId+"成功!");
			}
		}else
		{
			_log.info("网关注册到游戏失败,code="+code);
		}
	}
}
