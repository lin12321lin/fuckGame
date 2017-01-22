package net.jueb.fuckGame.script.center.http;

import io.netty.handler.codec.http.HttpRequest;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.script.center.AbstractCenterBaseScript;

public abstract class AbstractInnerHttpScript extends AbstractCenterBaseScript implements IInnerHttpScript {

	@Override
	public final void handleRequest(Request request) {
		handleRequest(request.getConnection(), (HttpRequest) request.getContent());
	}
	
	protected abstract void handleRequest(NetConnection connection, HttpRequest msg);
	
	@Override
	public void action() {
		
	}
}