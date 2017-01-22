package net.jueb.fuckGame.script.game.innerScript.io;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.game.ServerMain;
import net.jueb.fuckGame.game.ServerQueue;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.script.game.innerScript.AbstractInnerScript;

/**
 * @author Administrator
 */
public class MessageArrivedScript extends AbstractInnerScript{

	@Override
	public void action() {
		NetConnection conn=(NetConnection) getParams().get(0);
		GameMessage msg=(GameMessage) getParams().get(1);
		int code =msg.getCode();
		//系统内部消息范围1000-1999
		//大厅范围20000-2999;
		//游戏范围30000-3999;
		//网关消息范围4000-4999;
		IServerScript script=null;
		ByteBuffer byteBuffer = msg.getContent();
		short queue =ServerQueue.MAIN;
		switch (code) {
		default:
			break;
		}
		script =ScriptFactory.getInstance().buildHandleRequest(code,conn,byteBuffer);
		if(script!=null)
		{
			ServerMain.getInstance().getQueues().execute(queue, script);
		}else
		{
			_log.error("recvMsg,code="+code+"(0x"+Integer.toHexString(code)+"),script:"+script);
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_MessageArrived;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}
}
