package net.jueb.fuckGame.core.net.handlerImpl;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.util4j.net.nettyImpl.NettyConnection;

public class NetConntectionImpl extends NettyConnection implements NetConnection {

	public NetConntectionImpl(Channel channel) {
		super(channel);
	}
	
	@Override
	public void sendData(byte[] data) {
		if(isActive())
		{
			super.write(data);
		}
	}

	@Override
	public void sendData(byte[] data, int offset, int count) {
		if(isActive())
		{
			ByteBuf buf=PooledByteBufAllocator.DEFAULT.buffer();
			buf.writeBytes(data, offset, count);
			super.writeAndFlush(buf);
		}
	}

	@Override
	public void sendMessage(Object message) {
		if(isActive())
		{
			super.writeAndFlush(message);
		}
	}

	@Override
	public InetSocketAddress getRemote() {
		return super.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getLocal() {
		return super.getLocalAddress();
	}

	@Override
	public String getIP() {
		String ip="";
		if(channel!=null)
		{
			InetSocketAddress address=(InetSocketAddress) super.getRemoteAddress();
			ip=address.getHostString();
		}
		return ip;
	}

	@Override
	public void setId(int id) {
		this.id=id;
	}
}
