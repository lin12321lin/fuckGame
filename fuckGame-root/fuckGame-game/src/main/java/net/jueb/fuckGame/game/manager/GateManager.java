package net.jueb.fuckGame.game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.game.base.ConnectionKey;
import net.jueb.fuckGame.game.base.GateController;

/**
 * 网关管理器
 * @author Administrator
 */
public class GateManager {

	protected final Logger log=Log4jUtil.getLogger(getClass());
	private final Map<Integer,GateController> controllers=new ConcurrentHashMap<Integer,GateController>();
	private static GateManager instance;
	private static final Object lock0=new Object();
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	
	private GateManager(){};
	
	public static GateManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock0) {
				if(instance==null)
				{
					instance=new GateManager();
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
	public void regist(ServerInfo info,NetConnection conn)
	{
		if(info==null)
		{
			return ;
		}
		rwLock.writeLock().lock();
		try {
			conn.setAttribute(ConnectionKey.GATE_SERVER_ID, info.getServerId());
			GateController client=new GateController(info, conn);
			controllers.put(info.getServerId(), client);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * 移除游戏服务器
	 * @param serverId
	 */
	public void remove(int serverId)
	{
		rwLock.writeLock().lock();
		try {
			controllers.remove(serverId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
	}
	
	/**
	 * 如果是游戏连接则返回游戏服务器信息
	 * @param conn
	 * @return
	 */
	public GateController find(NetConnection conn)
	{
		rwLock.readLock().lock();
		try {
			if(conn.hasAttribute(ConnectionKey.GATE_SERVER_ID))
			{
				int serverId=(int) conn.getAttribute(ConnectionKey.GATE_SERVER_ID);
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
	public GateController get(int serverId)
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
	
	public Collection<GateController> getList()
	{
		rwLock.readLock().lock();
		try {
			return new ArrayList<GateController>(controllers.values());
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
			for(GateController conn:controllers.values())
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
			for(GateController conn:controllers.values())
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
