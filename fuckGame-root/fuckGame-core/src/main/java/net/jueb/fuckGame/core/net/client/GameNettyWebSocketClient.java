package net.jueb.fuckGame.core.net.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.client.websocket.NettyBinaryWebSocketClient;

public class GameNettyWebSocketClient extends NettyBinaryWebSocketClient implements NetClient{

	public GameNettyWebSocketClient(NettyClientConfig config, String host, int port, String url,
			ChannelInitializer<SocketChannel> channelInitializer) throws Exception {
		super(config, host, port, url, channelInitializer);
	}

	public GameNettyWebSocketClient(String host, int port, String url,
			ChannelInitializer<SocketChannel> channelInitializer) throws Exception {
		super(host, port, url, channelInitializer);
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
