package net.jueb.fuckGame.core.net.handlerImpl.handler;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.ssl.SslContext;
import net.jueb.util4j.net.JConnectionListener;
import net.jueb.util4j.net.nettyImpl.handler.http.HttpClientInitHandler;

public class InitalizerHttpClientHandler extends HttpClientInitHandler {

	public InitalizerHttpClientHandler(JConnectionListener<HttpResponse> listener, SslContext sslCtx) {
		super(listener, sslCtx);
	}

	public InitalizerHttpClientHandler(JConnectionListener<HttpResponse> listener) {
		super(listener);
	}
}
