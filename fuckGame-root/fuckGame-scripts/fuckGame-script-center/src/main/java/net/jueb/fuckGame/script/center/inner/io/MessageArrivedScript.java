package net.jueb.fuckGame.script.center.inner.io;

import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.center.ServerQueue;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

/**
 * @author Administrator
 */
public class MessageArrivedScript extends AbstractInnerScript{

	@Override
	public void action() {
		NetConnection conn=getParam(0);
		GameMessage msg=getParam(1);
		int code =msg.getCode();
		ByteBuffer byteBuffer = msg.getContent();
		IServerScript script=null;
		short queue =ServerQueue.MAIN;
		switch (code) {
			case GameMsgCode.Center_UserLogin:
				queue=ServerQueue.LOGIN;
				break;
		default:
			break;
		}
		script =ScriptFactory.getInstance().buildHandleRequest(code, conn,byteBuffer);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(queue, script);
			_log.trace("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),conn="+conn+",script:"+script);
		}else
		{
			_log.error("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),script:"+script);
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_MessageArrived;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}
}
