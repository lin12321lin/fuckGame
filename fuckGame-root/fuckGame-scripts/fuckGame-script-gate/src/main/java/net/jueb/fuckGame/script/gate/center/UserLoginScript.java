package net.jueb.fuckGame.script.gate.center;

import net.jueb.fuckGame.core.common.dto.UserLoginInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.util4j.cache.callBack.CallBack;

public class UserLoginScript extends AbstractCenterScript{

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		UserLoginInfo user=(UserLoginInfo) getParams().get(0);
		CallBack<ByteBuffer> callBack=(CallBack<ByteBuffer>) getParams().get(1);
		long callKey=ServerMain.getInstance().getCallBackCache().put(callBack);
		GameMessage msg=new GameMessage(getMessageCode());
		msg.getContent().writeLong(callKey);
		user.writeTo(msg.getContent());
		ServerMain.getInstance().getCenterClient().sendMessage(msg);
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_UserLogin;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		ByteBuffer buffer=clientBuffer;
		long callKey=buffer.readLong();
		CallBack<ByteBuffer> callBack=ServerMain.getInstance().getCallBackCache().poll(buffer, callKey);
		if(callBack!=null)
		{
			callBack.call(buffer);
		}
	}
}
