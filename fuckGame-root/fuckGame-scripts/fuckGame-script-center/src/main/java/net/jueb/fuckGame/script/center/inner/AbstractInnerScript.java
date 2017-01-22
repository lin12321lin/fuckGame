package net.jueb.fuckGame.script.center.inner;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.script.center.AbstractCenterBaseScript;

public abstract class AbstractInnerScript extends AbstractCenterBaseScript implements IInnerScript {

	@Override
	public final void handleRequest(Request request) {
		handleRequest(request.getConnection(), (ByteBuffer) request.getContent());
	}
	
	protected abstract void handleRequest(NetConnection connection, ByteBuffer msg);
	
	@Override
	public void action() {
		
	}
}