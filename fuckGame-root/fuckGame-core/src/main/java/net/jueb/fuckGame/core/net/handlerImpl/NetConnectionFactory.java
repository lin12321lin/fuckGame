package net.jueb.fuckGame.core.net.handlerImpl;

import io.netty.channel.Channel;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionFactory;

public class NetConnectionFactory implements JConnectionFactory{

	private static final NetConnectionFactory instance=new NetConnectionFactory();
	
	private NetConnectionFactory()
	{
		
	}
	
	public static NetConnectionFactory getInstance()
	{
		return instance;
	}
	
	@Override
	public JConnection buildConnection() {
		return new NetConntectionImpl(null);
	}

	@Override
	public JConnection buildConnection(Object arg) {
		if(arg instanceof Channel)
		{
			return new NetConntectionImpl((Channel)arg);
		}
		return null;
	}
}
