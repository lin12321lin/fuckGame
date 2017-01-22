package net.jueb.fuckGame.core.net.server;

import java.net.InetSocketAddress;
import java.net.UnknownServiceException;

import io.netty.channel.ChannelHandler;
import net.jueb.util4j.net.nettyImpl.server.NettyServer;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;

public class GameNettyServer extends NettyServer implements NetServer{

	public GameNettyServer(InetSocketAddress local, ChannelHandler handler) {
		super(local, handler);
	}
	public GameNettyServer(String host,int port, ChannelHandler handler) {
		super(new InetSocketAddress(host, port), handler);
	}

	public GameNettyServer(NettyServerConfig config, InetSocketAddress local,
			ChannelHandler handler) {
		super(config, local, handler);
	}
	public GameNettyServer(NettyServerConfig config, String host,int port,
			ChannelHandler handler) {
		super(config, new InetSocketAddress(host, port), handler);
	}

	@Override
	public void startNetWork() throws UnknownServiceException {
		if(!super.start())
		{
			throw new UnknownServiceException("端口服务启动失败");
		}
	}
}
