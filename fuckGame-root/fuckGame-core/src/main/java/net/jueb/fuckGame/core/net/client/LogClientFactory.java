package net.jueb.fuckGame.core.net.client;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.handler.logging.LogLevel;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.handler.InitalizerProtocolHandler;
import net.jueb.fuckGame.core.net.handlerImpl.listener.GameClientConnectionListener;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;

public class LogClientFactory {
	
	public static NetClient buildMultiLogClient(NettyClientConfig config, String host, int port,ScheduledExecutorService reconnect)
	{
		GameNettyClient client=new GameNettyClient(config,host,port,new InitalizerProtocolHandler(new GameClientConnectionListener(){

			@Override
			public void messageArrived(NetConnection conn, GameMessage msg) {
				
			}

			@Override
			public void connectionOpened(NetConnection connection) {
				
			}

			@Override
			public void connectionClosed(NetConnection connection) {
				
			}
		}));
		client.setName("日志客户端");
		client.enableReconnect(reconnect, TimeUnit.SECONDS.toMillis(10));
		return client;
	}
	

	public static NetClient buildLogClient(String host, int port,ScheduledExecutorService reconnect)
	{
		NettyClientConfig config=new NettyClientConfig() ;
		config.setLevel(LogLevel.TRACE);
		GameNettyClient client=new GameNettyClient(config,host,port,new InitalizerProtocolHandler(new GameClientConnectionListener(){

			@Override
			public void messageArrived(NetConnection conn, GameMessage msg) {
				
			}

			@Override
			public void connectionOpened(NetConnection connection) {
				
			}

			@Override
			public void connectionClosed(NetConnection connection) {
				
			}
		}));
		client.setName("日志客户端");
		client.enableReconnect(reconnect, TimeUnit.SECONDS.toMillis(10));
		return client;
	}
	
}
