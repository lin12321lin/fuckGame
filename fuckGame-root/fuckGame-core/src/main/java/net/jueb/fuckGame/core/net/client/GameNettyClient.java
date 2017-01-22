package net.jueb.fuckGame.core.net.client;

import java.net.InetSocketAddress;
import io.netty.channel.ChannelHandler;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.util4j.net.nettyImpl.client.NettyClient;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;

public class GameNettyClient extends NettyClient implements NetClient{

	public GameNettyClient(NettyClientConfig config,String host,int port,
			ChannelHandler handler) {
		super(config, new InetSocketAddress(host, port), handler);
	}

	public GameNettyClient(String host,int port, ChannelHandler handler) {
		super(host, port, handler);
	}

	@Override
	public void close() {
		super.stop();
	}

	@Override
	public void sendMessage(GameMessage msg) {
		super.sendObject(msg);
		super.flush();
	}

	@Override
	public void sendData(byte[] data, int offset, int count) {
		byte[] data2=new byte[count];
		System.arraycopy(data, 0, data2, offset, count);
		super.sendData(data2);
		super.flush();
	}

	@Override
	public boolean isActive() {
		return super.isConnected();
	}
	@Override
	public void startNetWork() {
		super.start();
	}
}
