package net.jueb.fuckGame.core.net.handlerImpl.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.handler.codec.http.HttpResponse;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.NetConnectionListener;
import net.jueb.fuckGame.core.net.handlerImpl.NetConnectionFactory;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionFactory;
import net.jueb.util4j.net.JConnectionListener;

/**
 * 客户端链接监听
 * @author Administrator
 */
public abstract class GameHttpClientConnectionListener implements JConnectionListener<HttpResponse>,NetConnectionListener<HttpResponse>{

	protected final Logger _log = LoggerFactory.getLogger(getClass());
	
	
	@Override
	public final void connectionClosed(JConnection connection) {
		NetConnection conn=(NetConnection)connection;
		connectionClosed(conn);
	}
	
	@Override
	public final void connectionOpened(JConnection connection) {
		NetConnection conn=(NetConnection)connection;
		connectionOpened(conn);
	}
	
	@Override
	public final void messageArrived(JConnection conn, HttpResponse msg) {
		messageArrived((NetConnection) conn,msg);
	}
	
	public final JConnectionFactory getConnectionFactory() {
		return NetConnectionFactory.getInstance();
	}
	
	public abstract void messageArrived(NetConnection conn,HttpResponse msg);
    
	public abstract void connectionOpened(NetConnection connection);

	public abstract void connectionClosed(NetConnection connection);
}
