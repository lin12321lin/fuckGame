package net.jueb.fuckGame.script.game.innerScript;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.script.game.AbstractGameBaseScript;

public abstract class AbstractInnerScript extends AbstractGameBaseScript implements IInnerScript {

	@Override
	public final void handleRequest(Request request) {
		handleRequest(request.getConnection(), (ByteBuffer) request.getContent());
	}
	
	protected abstract void handleRequest(NetConnection connection, ByteBuffer msg);
}
