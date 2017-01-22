package net.jueb.fuckGame.script.gate.center;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.common.dto.Address;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.ServerConfig;
import net.jueb.fuckGame.gate.ServerMain;

public class GateRegScript extends AbstractCenterScript{

	@Override
	public void action() {
		_log.debug("请求大厅网关注册");
		ByteBuffer buffer=new ByteBuffer();
		ServerInfo info=new ServerInfo();
		info.setAddress(new Address(ServerConfig.LAN_HOST,ServerConfig.LAN_PORT));
		info.setServerId(ServerConfig.SERVER_ID);
		info.writeTo(buffer);
		GameMessage message=new GameMessage(getMessageCode(),buffer);
		NetConnection conn=(NetConnection) getParams().get(0);
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
		return GameMsgCode.Center_GateReg;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		ByteBuffer buffer=clientBuffer;
		int code=buffer.readInt();
		if(code==GameErrCode.Succeed.value())
		{
			ServerMain.getInstance().setRegCenter(true);
			_log.info("大厅网关注册成功!");
		}else
		{
			ServerMain.getInstance().setRegCenter(false);
			_log.error("大厅网关注册失败,code="+code);
		}
	}
}
