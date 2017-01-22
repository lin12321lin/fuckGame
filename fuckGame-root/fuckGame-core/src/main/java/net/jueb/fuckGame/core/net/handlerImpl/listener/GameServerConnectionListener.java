package net.jueb.fuckGame.core.net.handlerImpl.listener;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.NetConnectionListener;
import net.jueb.fuckGame.core.net.handlerImpl.NetConnectionFactory;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.util4j.net.JConnection;
import net.jueb.util4j.net.JConnectionFactory;
import net.jueb.util4j.net.nettyImpl.listener.HeartAbleConnectionListener;

/**
 * 服务端连接监听
 * @author Administrator
 */
public abstract class GameServerConnectionListener extends HeartAbleConnectionListener<GameMessage> implements NetConnectionListener<GameMessage>{
	protected final Logger _log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 心跳验证间隔(仅当空闲时)
	 */
	public static long HeartIntervalMills=TimeUnit.SECONDS.toMillis(5);
	
	/**
	 * 主动心跳请求发送
	 */
	@Override
	public long getWriterIdleTimeMills() {
		return HeartIntervalMills;
	}
	
	/**
	 * (保证5-10秒内有消息来回)
	 * 没有任何请求或者3次心跳请求没有回复则关闭连接(2次会有临界点,如果回复比较快)
	 */
	@Override
	public long getReaderIdleTimeMills() {
		return HeartIntervalMills;
	}
	
	/**
	 * 3次心跳时间没有操作则关闭连接
	 */
	@Override
	public long getAllIdleTimeMills() {
		return getWriterIdleTimeMills()*4;
	}
	
	//################心跳配置开始############################
	/**
	 * 发送心跳请求
	 */
	protected void sendHeartReq(JConnection connection)
	{
		ByteBuffer buffer = new ByteBuffer();
		GameMessage appMsg = new GameMessage(GameMsgCode.Heart_Req,buffer);
		connection.writeAndFlush(appMsg);
	}
	
	/**
	 * 发送心跳回复
	 */
	protected void sendHeartRsp(JConnection connection)
	{
		ByteBuffer buffer = new ByteBuffer();
		GameMessage appMsg = new GameMessage(GameMsgCode.Heart_Rsp,buffer);
		connection.writeAndFlush(appMsg);
	}

	@Override
	protected boolean isHeartReq(GameMessage msg) {
		return msg.getCode()==GameMsgCode.Heart_Req;
	}

	//################心跳配置结束############################
	
	
	//################桥接模式开始############################
	@Override
	protected void doMessageArrived(JConnection conn, GameMessage msg) {
		messageArrived((NetConnection) conn,msg);
	}

	public final JConnectionFactory getConnectionFactory() {
		return NetConnectionFactory.getInstance();
	}

	@Override
	public final void connectionClosed(JConnection connection) {
		NetConnection conn=(NetConnection)connection;
		connectionClosed(conn);
	}

	@Override
	public final void connectionOpened(JConnection connection) {
		NetConnection conn=(NetConnection)connection;
		connectionOpened(conn);
	}
	//################桥接模式结束############################

	public abstract void messageArrived(NetConnection conn,GameMessage msg);
    
	public abstract void connectionOpened(NetConnection connection);

	public abstract void connectionClosed(NetConnection connection);			
}