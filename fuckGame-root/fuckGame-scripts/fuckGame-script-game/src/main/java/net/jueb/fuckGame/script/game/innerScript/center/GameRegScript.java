package net.jueb.fuckGame.script.game.innerScript.center;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.common.dto.Address;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.ServerMain;

public class GameRegScript extends AbstractCenterScript{

	@Override
	public void action() {
		_log.debug("请求注册到大厅");
		ByteBuffer buffer=new ByteBuffer();
		ServerInfo info=new ServerInfo();
		info.setAddress(new Address(ServerConfig.LAN_HOST,ServerConfig.LAN_PORT));
		info.setServerId(ServerConfig.SERVER_ID);
		info.writeTo(buffer);
		GameMessage message=new GameMessage(getMessageCode(),buffer);
		NetConnection conn=getParamOrNull(0);
		if(conn!=null)
		{
			conn.sendMessage(message);
		}else
		{
			ServerMain.getInstance().getCenterClient().sendMessage(message);
		}
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		ByteBuffer buffer=clientBuffer;
		int code=buffer.readInt();
		if(code==GameErrCode.Succeed.value())
		{
			ServerMain.getInstance().setRegCenter(true);
			_log.info("注册到大厅成功!");
		}else
		{
			ServerMain.getInstance().setRegCenter(false);
			_log.error("注册到大厅失败,code="+code);
		}
	}
}
