package net.jueb.fuckGame.core.net.handlerImpl.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import net.jueb.fuckGame.core.net.codec.ProtocolDecoder;
import net.jueb.fuckGame.core.net.codec.ProtocolEncoder;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.util4j.net.JConnectionIdleListener;
import net.jueb.util4j.net.nettyImpl.handler.listenerHandler.adapter.IdleListenerHandlerAdapter;

public class InitalizerProtocolHandler extends ChannelInitializer<SocketChannel> {

	private JConnectionIdleListener<GameMessage> listener;

	public InitalizerProtocolHandler(JConnectionIdleListener<GameMessage> listener) {
		this.listener = listener;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast(new ProtocolDecoder());
		p.addLast(new ProtocolEncoder());
		p.addLast(new IdleListenerHandlerAdapter<GameMessage>(listener));
	}
}