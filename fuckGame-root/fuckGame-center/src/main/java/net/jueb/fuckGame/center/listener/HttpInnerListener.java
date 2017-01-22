package net.jueb.fuckGame.center.listener;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.center.ServerQueue;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.listener.GameHttpServerConnectionListener;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.util4j.hotSwap.classFactory.IScript;

public class HttpInnerListener extends GameHttpServerConnectionListener{

	@Override
	public void messageArrived(NetConnection conn, HttpRequest msg) {
		ReferenceCountUtil.retain(msg);//因为此方法执行完后就会回收msg,所以这里保留一次
		IScript script=ScriptFactory.getInstance().buildHandleRequest(GameMsgCode.HttpRequest, conn, msg);
		ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, script);
	}

	@Override
	public void connectionOpened(NetConnection connection) {
		
	}

	@Override
	public void connectionClosed(NetConnection connection) {
		
	}
}
