package net.jueb.fuckGame.core.net.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;

/**
 * 总长度=长度头(4)+消息码(2)+消息数据body
 * 长度头值=2+dataLen
 * 校验位=校验位后面的所有字节计算值(code+data)
 */
public class ProtocolEncoder extends MessageToByteEncoder<GameMessage> {
	protected final Logger _log = LoggerFactory.getLogger(getClass());
	protected final static int PACK_MAX_SIZE = 64 * 1024 * 1024;

	public ProtocolEncoder() {
	}
	
	@Override
	public void encode(ChannelHandlerContext ctx, GameMessage appMsg, ByteBuf out) throws Exception {
		ByteBuffer content = appMsg.getContent();
		content.reset();
		short code=appMsg.getCode();
		byte[] body=content.getBytes();//如果业务层是广播的,则为了防止底层发生线程的readIndex变化,所以直接复制不读取
		int lengthHead=2+body.length;//长度头=2字节消息码+数据长度
		out.writeInt(lengthHead);
		out.writeShort(code);
		out.writeBytes(body);
	}
}
