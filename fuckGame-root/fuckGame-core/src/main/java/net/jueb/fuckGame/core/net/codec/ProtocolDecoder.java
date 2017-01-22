package net.jueb.fuckGame.core.net.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;

/**
 * 总长度=长度头(4)+消息码(2)+数据body
 * 长度头值=4+2+dataLen
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
	public static int DEFALUT_MAX_PACK_SIZE = 64 * 1024 * 1024;
	protected final Logger _log = LoggerFactory.getLogger(getClass());
	private int maxPackSize = DEFALUT_MAX_PACK_SIZE;

	public ProtocolDecoder() {
	}
	public ProtocolDecoder(int maxPackSize) {
		if(maxPackSize<=0)
		{
			this.maxPackSize=DEFALUT_MAX_PACK_SIZE;
		}else
		{
			this.maxPackSize=maxPackSize;
		}
	}
	
	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
			throws Exception {
		int readableBytes = in.readableBytes();
		if (readableBytes < 6) {//6=总长度头(4)+消息码(2)
			return;
		}
		in.markReaderIndex();
		int lengthHead = in.readInt();//长度头4字节
		short code=in.readShort();//消息码2字节
		int dataLen=lengthHead-2;//去掉校验位大小消息头的数据长度
		//检查包长度
		if(maxPackSize > 0 && lengthHead>maxPackSize)
		{
			in.resetReaderIndex();
			_log.error(ctx.channel().toString()+":消息长度超出范围lengthHead="+lengthHead+",code="+code+",dataLen="+dataLen);
			ctx.close();
			return;
		}
		//检查数据长度
		if(dataLen<0)
		{
			in.resetReaderIndex();
			_log.error(ctx.channel().toString()+":消息数据长度错误lengthHead="+lengthHead+",code="+code+",dataLen="+dataLen);
			ctx.close();
			return;
		}
		//检查数据是否完整
		if(dataLen>0 && in.readableBytes()<dataLen)
		{//如果剩余数据还未到达则等待下一次读事件(排除心跳)
			in.resetReaderIndex();
			return;
		}
		byte[] contentData=new byte[dataLen];//消息数据
		in.readBytes(contentData, 0, contentData.length);
		GameMessage message = new GameMessage(code,new ByteBuffer(contentData));
		out.add(message);
		_log.trace("decoded AppMessage:"+message+",ctx:"+ctx.pipeline().names());
	}
}
