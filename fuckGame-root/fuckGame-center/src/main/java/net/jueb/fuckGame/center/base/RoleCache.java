package net.jueb.fuckGame.center.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.util.Log4jUtil;

public class RoleCache {
	protected Logger log=Log4jUtil.getLogger(getClass());
	private static RoleCache instance;
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	private final Map<Long,Role> roles=new HashMap<Long,Role>();
	//一个平台只有一个唯一的uid
	private final Map<String,Long> uid_roles=new HashMap<String,Long>();
	private final Set<Long> updates_=new HashSet<Long>();//更新信息队列
	 
	public static RoleCache getInstance(){
	     if(instance==null){
	       instance=new RoleCache();
	     }
	     return instance;
	}

	protected void unSafePut(Role role)
	{
		long id=role.getId();
		String uid=role.getUid();
		roles.put(id,role);
		uid_roles.put(uid,id);
	}
	
	public Role put(Role role){
		if(role==null)
		{
			return null;
		}
		rwLock.writeLock().lock();
		try {
			unSafePut(role);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
		return role;
	}
	
	public void put(Collection<Role> roles){
		if(roles==null)
		{
			return ;
		}
		rwLock.writeLock().lock();
		try {
			for(Role role:roles)
			{
				unSafePut(role);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
	}
	
	public void loadRoles(List<Role> roles)
	{
		put(roles);
	}
	
	public Role getRoleById(long id)
	{
		rwLock.readLock().lock();
		try {
			return roles.get(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}
	
	/**
	 * uid不唯一,不同平台可能有相同的uid
	 * @param uid
	 * @param pn
	 * @return
	 */
	public Role getRoleByUid(String uid)
	{
		rwLock.readLock().lock();
		try {
			Long id=uid_roles.get(uid);
			if(id==null)
			{
				return null;
			}
			return roles.get(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return null;
	}
	
	/**
	 * 是否已经存在电话号码
	 * @param phone
	 * @return
	 */
	public boolean hasPhone(String phone)
	{
		rwLock.readLock().lock();
		try {
			for(Role role:roles.values())
			{
				String phone_=role.getPhone();
				if(phone_!=null && phone_.equals(phone_))
				{
					return true;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return false;
	}
	
	public Collection<Role> getRoles()
	{
		rwLock.readLock().lock();
		Set<Role> set=new HashSet<Role>();
		try {
			set.addAll(roles.values());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return set;
	}
	
	/**
	 * 获取所有角色id
	 * @return
	 */
	public Set<Long> roleIds()
	{
		rwLock.readLock().lock();
		Set<Long> set=new HashSet<Long>();
		try {
			set.addAll(roles.keySet());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
		return set;
	}
	
	private final ReentrantLock uplock=new ReentrantLock();
	
	
	/**
	 * 更新标记
	 * @param id
	 */
	public void updateMark(long id)
	{
		mark(id);
	}
	
	protected void mark(long id)
	{
		uplock.lock();
		try {
			updates_.add(id);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			uplock.unlock();
		}
	}
	
	
	/**
	 * 更新标记
	 * @param id
	 */
	public void updateMark(Set<Long> ids)
	{
		uplock.lock();
		try {
			for(Long id:ids)
			{
				updates_.add(id);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			uplock.unlock();
		}
	}
	
	/**
	 * 弹出所有待更新的
	 * @return
	 */
	public Set<Long> pollUpdates() {
		uplock.lock();
		Set<Long> ids=new HashSet<Long>();
		try {
			ids.addAll(updates_);
			updates_.clear();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			uplock.unlock();
		}
		return ids;
	}
}
