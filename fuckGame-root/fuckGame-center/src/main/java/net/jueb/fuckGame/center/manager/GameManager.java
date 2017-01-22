package net.jueb.fuckGame.center.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import net.jueb.fuckGame.center.base.ConnectionKey;
import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.util.Log4jUtil;

/**
 * 游戏管理器
 * 注意,一个gameId可能会有多个id对应
 * id等于邀请码的前2位
 * @author Administrator
 */
public class GameManager {

	protected final Logger log=Log4jUtil.getLogger(getClass());
	private final Map<Integer,GameController> controllers=new ConcurrentHashMap<Integer,GameController>();
	private static GameManager instance;
	private static final Object lock0=new Object();
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	
	private GameManager(){
	};
	
	public static GameManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock0) {
				if(instance==null)
				{
					instance=new GameManager();
				}
			}
		}
		return instance;
	}
	
	public boolean has(int serverId)
	{
		rwLock.readLock().lock();
		try {
			return controllers.get(serverId)!=null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return false;
	}
	
	/**
	 * 注册游戏服务器
	 * @param info
	 * @param conn
	 */
	public GameController regist(ServerInfo info,NetConnection conn)
	{
		rwLock.writeLock().lock();
		try {
			conn.setAttribute(ConnectionKey.GAME_SERVER_ID,info.getServerId());
			GameController client=new GameController(info, conn);
			controllers.put(info.getServerId(), client);
			return client;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
		return null;
	}
	
	/**
	 * 移除游戏服务器
	 * @param serverId
	 */
	public GameController remove(int serverId)
	{
		GameController gc=null;
		rwLock.writeLock().lock();
		try {
			gc=controllers.remove(serverId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
		return gc;
	}
	
	/**
	 * 如果是游戏连接则返回游戏服务器信息
	 * @param conn
	 * @return
	 */
	public GameController find(NetConnection conn)
	{
		rwLock.readLock().lock();
		try {
			if(conn.hasAttribute(ConnectionKey.GAME_SERVER_ID))
			{
				int serverId=(int) conn.getAttribute(ConnectionKey.GAME_SERVER_ID);
				return controllers.get(serverId);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}
	
	/**
	 * 如果是游戏连接则返回游戏服务器信息
	 * @param conn
	 * @return
	 */
	public GameController getById(int serverId)
	{
		rwLock.readLock().lock();
		try {
			return controllers.get(serverId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}
	
	public Collection<GameController> getList()
	{
		rwLock.readLock().lock();
		try {
			return new ArrayList<GameController>(controllers.values());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}
	
	/**
	 * 广播消息
	 * @param msg
	 */
	public void broadcast(GameMessage msg)
	{
		rwLock.readLock().lock();
		try {
			for(GameController conn:controllers.values())
			{
				conn.getConnection().sendMessage(msg);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
	}
	
	/**
	 * 广播消息
	 * @param msg
	 */
	public void broadcast(GameMessage msg,NetConnection exclude)
	{
		rwLock.readLock().lock();
		try {
			for(GameController conn:controllers.values())
			{
				if(conn.getConnection()!=exclude)
				{
					conn.getConnection().sendMessage(msg);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
	}
}
