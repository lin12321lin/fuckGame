package net.jueb.fuckGame.game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.game.base.RoleController;

/**
 * 网关管理器
 * @author Administrator
 */
public class RoleManager {

	protected final Logger log=Log4jUtil.getLogger(getClass());
	private final Map<Long,RoleController> controllers=new HashMap<Long,RoleController>();
	private static RoleManager instance;
	private static final Object lock0=new Object();
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	
	private RoleManager(){};
	
	public static RoleManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock0) {
				if(instance==null)
				{
					instance=new RoleManager();
				}
			}
		}
		return instance;
	}
	
	public void add(RoleController rc)
	{
		rwLock.readLock().lock();
		try {
			controllers.put(rc.getId(), rc);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
	}
	
	public RoleController getById(long roleId)
	{
		rwLock.readLock().lock();
		try {
			return controllers.get(roleId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}

	/**
	 * 移除游戏服务器
	 * @param serverId
	 */
	public RoleController remove(long roleId)
	{
		rwLock.writeLock().lock();
		RoleController rc=null;
		try {
			rc=controllers.remove(roleId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
		return rc;
	}

	public Collection<RoleController> getList()
	{
		rwLock.readLock().lock();
		try {
			return new ArrayList<RoleController>(controllers.values());
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
			for(RoleController role:controllers.values())
			{
				role.sendMsg(msg);
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
	public void broadcast(GameMessage msg,long excludeRoleId)
	{
		rwLock.readLock().lock();
		try {
			for(RoleController role:controllers.values())
			{
				if(role.getId()!=excludeRoleId)
				{
					role.sendMsg(msg);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
	}
}
