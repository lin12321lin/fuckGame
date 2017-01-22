package net.jueb.fuckGame.center.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import net.jueb.fuckGame.core.util.Log4jUtil;

/**
 * 角色锁定管理器
 * @author Administrator
 */
public class RoleGameLockManager {
	protected final Logger _log=Log4jUtil.getLogger(getClass());
	private static RoleGameLockManager instance;
	private static final Object lock=new Object();
	
	private final Map<Long,Integer> role_server=new ConcurrentHashMap<Long,Integer>();
	
	private RoleGameLockManager(){};
	
	public static RoleGameLockManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock) {
				if(instance==null)
				{
					instance=new RoleGameLockManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 锁定角色
	 * @param roleId
	 * @param serverId
	 */
	public void lock(long roleId,int serverId)
	{
		role_server.put(roleId, serverId);
	}
	
	public void unLock(long roleId)
	{
		role_server.remove(roleId);
	}
	
	public boolean isLock(long roleId)
	{
		return role_server.containsKey(roleId);
	}
	
	/**
	 * 返回当前角色被锁定的服务器ID
	 * -1表示未锁定
	 * @param roleId
	 * @return
	 */
	public int lockedServer(long roleId)
	{
		int id=-1;
		Integer id_=role_server.get(roleId);
		if(id_!=null)
		{
			id=id_;
		}
		return id;
	}
	
	/**
	 * 解锁所有该服务器角色
	 * @param serverId
	 */
	public Collection<Long> unlockByServer(int serverId)
	{
		Collection<Long> ids=findLockRoles(serverId);
		for(Long id:ids)
		{
			role_server.remove(id);
		}
		return ids;
	}
	
	/**
	 * 找到锁定在该服务器的角色ID集合
	 * @param serverId
	 * @return
	 */
	public Collection<Long> findLockRoles(int serverId)
	{
		Collection<Long> ids=new HashSet<Long>();
		for(Entry<Long, Integer> entry:role_server.entrySet())
		{
			if(entry.getValue()==serverId)
			{
				ids.add(entry.getKey());
			}
		}
		return ids;
	}
}
