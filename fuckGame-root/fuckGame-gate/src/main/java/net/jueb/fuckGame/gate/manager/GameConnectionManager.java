package net.jueb.fuckGame.gate.manager;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

import io.netty.channel.ChannelFuture;
import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.handlerImpl.handler.InitalizerProtocolHandler;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.base.ConnectionKey;
import net.jueb.fuckGame.gate.listener.GameClientListener;
import net.jueb.util4j.net.nettyImpl.client.connections.ConnectionBuilder;

/**
 * 游戏服务器连接管理器
 * @author Administrator
 */
public class GameConnectionManager {

	private final Logger log=Log4jUtil.getLogger(getClass());
	private static GameConnectionManager instance;
	private static final Object lock_0=new Object();
	private final Map<Integer,NetConnection> gameConns=new HashMap<Integer,NetConnection>();
	private final ReentrantLock lock=new ReentrantLock();
	
	private GameConnectionManager() {
	}

	public static GameConnectionManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock_0) {
				if(instance==null)
				{
					instance=new GameConnectionManager();
				}
			}
		}
		return instance;
	}
	
	public void initServerConnection(Collection<ServerInfo> games)
	{
		for(ServerInfo g:games)
		{
			initServerConnection(g);
		}
	}
	
	public String info()
	{
		lock.lock();
		try {
			return gameConns.toString();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return null;
	}
	public ConnectionBuilder cb=ServerMain.getInstance().getGameClientConnector();
	
	public boolean initServerConnection(ServerInfo game)
	{
		lock.lock();
		boolean result=false;
		try {
			ChannelFuture cf=cb.connect(
					new InetSocketAddress(game.getAddress().getHost(), game.getAddress().getPort()), 
					new InitalizerProtocolHandler(new GameClientListener()));
			if(cf!=null && cf.isDone() && cf.channel().isRegistered())
			{
				 result=true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return result;
	}
	
	public NetConnection getServerConnection(int serverId)
	{
		lock.lock();
		try {
			return gameConns.get(serverId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return null;
	}
	
	public void addServerConnection(int serverId,NetConnection conn)
	{
		lock.lock();
		try {
			conn.setAttribute(ConnectionKey.GameServerId, serverId);
			gameConns.put(serverId, conn);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
	}
	
	public int findServerId(NetConnection conn)
	{
		if(conn==null)
		{
			return 0;
		}
		lock.lock();
		try {
			for(Entry<Integer, NetConnection> e:gameConns.entrySet())
			{
				if(e.getValue()==conn)
				{
					return e.getKey();
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return 0;
	}
	
	/**
	 * 移除链接
	 * @param serverId
	 * @return
	 */
	public NetConnection removeServerConnection(int serverId)
	{
		lock.lock();
		try {
			NetConnection conn=gameConns.remove(serverId);;
			if(conn!=null)
			{
				conn.removeAttribute(ConnectionKey.GameServerId);
			}
			return conn;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return null;
	}
	
	public NetConnection find(int serverId)
	{
		lock.lock();
		try {
			return gameConns.get(serverId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return null;
	}
	
	public boolean sendMessage(int serverId,GameMessage msg)
	{
		lock.lock();
		try {
			NetConnection conn=gameConns.get(serverId);
			if(conn!=null && conn.isActive())
			{
				conn.sendMessage(msg);
				return true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
		return false;
	}
	
	/**
	 * 广播消息
	 * @param msg
	 */
	public void broadcast(GameMessage msg)
	{
		lock.lock();
		try {
			for(NetConnection conn:gameConns.values())
			{
				conn.sendMessage(msg);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
	}
	
	/**
	 * 广播消息
	 * @param msg
	 */
	public void broadcast(GameMessage msg,NetConnection exclude)
	{
		lock.lock();
		try {
			for(NetConnection conn:gameConns.values())
			{
				if(conn==exclude)
				{
					continue;
				}
				conn.sendMessage(msg);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			lock.unlock();
		}
	}
}
