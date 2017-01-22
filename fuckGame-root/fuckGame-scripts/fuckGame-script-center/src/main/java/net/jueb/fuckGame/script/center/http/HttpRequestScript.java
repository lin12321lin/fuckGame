package net.jueb.fuckGame.script.center.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.NetConntectionImpl;
import net.jueb.fuckGame.core.net.message.GameMsgCode;

public class HttpRequestScript extends AbstractInnerHttpScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.HttpRequest;
	}
	
	NetConnection conn;
	HttpRequest httpRequest;
	private void response(String response) {
		NetConntectionImpl c=(NetConntectionImpl)conn;
		FullHttpResponse  httpResponse= new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
		httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain; charset=UTF-8");
		c.getChannel().writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
	}


	@Override
	protected void handleRequest(NetConnection connection,HttpRequest msg) {
		this.conn=connection;
		this.httpRequest=msg;
		_log.debug("收到http消息:conn="+conn+",httpRequest="+httpRequest);
		Map<String,String> args=new HashMap<>();
		QueryStringDecoder decode=new QueryStringDecoder(httpRequest.uri());
		String path=decode.path();
		if(msg.method()==HttpMethod.POST)
		{
			if(httpRequest instanceof FullHttpRequest)
			{
				FullHttpRequest request=(FullHttpRequest) httpRequest;
				HttpPostRequestDecoder d=new HttpPostRequestDecoder(request);
				for(InterfaceHttpData data:d.getBodyHttpDatas())
				{
					if(data.getHttpDataType()==HttpDataType.Attribute)
					{
						Attribute idata=(Attribute)data;
						try {
							args.put(data.getName(),idata.getValue());
						} catch (IOException e) {
							_log.error(e.getMessage(),e);
						}
					}
				}
			}
		}else
		{
			for(Entry<String, List<String>> e:decode.parameters().entrySet())
			{
				args.put(e.getKey(), e.getValue().get(0));
			}
		}
		switch (path) {
		case "/test":
			response("测试接口");
			break;
		default:
			response("请求不支持");
			break;
		}
		ReferenceCountUtil.release(httpRequest);
	}
}
