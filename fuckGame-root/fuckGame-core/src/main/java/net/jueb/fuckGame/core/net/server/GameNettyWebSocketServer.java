package net.jueb.fuckGame.core.net.server;

import java.net.UnknownServiceException;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.net.nettyImpl.server.NettyWebSocketServer;

public class GameNettyWebSocketServer extends NettyWebSocketServer implements NetServer{
	
	public GameNettyWebSocketServer(NettyServerConfig config, String host, int port, String uri,
			ChannelInboundHandlerAdapter handler) {
		super(config, host, port, uri, handler);
	}

	public GameNettyWebSocketServer(String host, int port, String uri,
			ChannelInboundHandlerAdapter handler) {
		super(host, port, uri, handler);
	}

	@Override
	public void startNetWork() throws UnknownServiceException {
		if(!super.start())
		{
			throw new UnknownServiceException("端口服务启动失败");
		}
	}
}
