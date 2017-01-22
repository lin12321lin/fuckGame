package net.jueb.fuckGame.script.game.innerScript.center;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.script.game.AbstractGameBaseScript;

public abstract class AbstractCenterScript extends AbstractGameBaseScript implements ICenterScript{

	@Override
	public final void handleRequest(Request request) {
		handleRequest(request.getConnection(), (ByteBuffer) request.getContent());
	}
	
	protected abstract void handleRequest(NetConnection connection, ByteBuffer msg);
}
