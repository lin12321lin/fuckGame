package net.jueb.fuckGame.core.net.handlerImpl.handler;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.JConnectionListener;
import net.jueb.util4j.net.nettyImpl.handler.http.HttpServerInitHandler;

public class InitalizerHttpServerHandler extends HttpServerInitHandler {

	public InitalizerHttpServerHandler(JConnectionListener<HttpRequest> listener, SslContext sslCtx) {
		super(listener, sslCtx);
	}

	public InitalizerHttpServerHandler(JConnectionListener<HttpRequest> listener) {
		super(listener);
	}
}
