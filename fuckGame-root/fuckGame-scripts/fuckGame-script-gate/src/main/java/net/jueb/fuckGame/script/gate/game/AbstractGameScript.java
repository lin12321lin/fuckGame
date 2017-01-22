package net.jueb.fuckGame.script.gate.game;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.script.gate.AbstractGateBaseScript;

public abstract class AbstractGameScript extends AbstractGateBaseScript implements IGameScript{

	
	@Override
	public final void handleRequest(Request request) {
		handleRequest(request.getConnection(), (ByteBuffer) request.getContent());
	}
	
	protected abstract void handleRequest(NetConnection connection, ByteBuffer msg);
}
