package net.jueb.fuckGame.game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;

import net.jueb.fuckGame.core.common.dto.RoomNumber;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.base.RoomController;

/**
 * 网关管理器
 * @author Administrator
 */
public class RoomManager {

	protected final Logger log=Log4jUtil.getLogger(getClass());
	private final Map<Integer,RoomController> controllers=new ConcurrentHashMap<Integer,RoomController>();
	private static RoomManager instance;
	private static final Object lock0=new Object();
	private final ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock();
	
	private RoomManager(){};
	
	public static RoomManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock0) {
				if(instance==null)
				{
					instance=new RoomManager();
				}
			}
		}
		return instance;
	}
	
	int startRoomId=1000;
	int endRoomId=9999;
	
	/**
	 * 创建房间
	 * @return
	 */
	public RoomController createRoom()
	{
		rwLock.writeLock().lock();
		try {
			List<Integer> roomIds=new ArrayList<>();
			for(int i=startRoomId;i<=endRoomId;i++)
			{
				if(!controllers.containsKey(i))
				{
					roomIds.add(i);
				}
			}
			if(roomIds.isEmpty())
			{//没有可用房间号
				return null;
			}
			Collections.shuffle(roomIds);
			int roomId=roomIds.get(0);
			RoomNumber romNumber=RoomNumber.valueOf(ServerConfig.SERVER_ID, roomId);
			RoomController table=new RoomController(romNumber);
			controllers.put(table.getNumber().getRoomId(), table);
			return controllers.get(table.getNumber().getRoomId());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
		return null;
	}
	
	public RoomController getById(int roomId)
	{
		rwLock.readLock().lock();
		try {
			return controllers.get(roomId);
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
	public RoomController remove(int roomId)
	{
		rwLock.writeLock().lock();
		RoomController rc=null;
		try {
			rc=controllers.remove(roomId);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.writeLock().unlock();
		}
		return rc;
	}

	public Collection<RoomController> getList()
	{
		rwLock.readLock().lock();
		try {
			return new ArrayList<RoomController>(controllers.values());
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
			for(RoomController table:controllers.values())
			{
				table.broadcast(msg);
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
	public void broadcast(GameMessage msg,int excludeRoomId)
	{
		rwLock.readLock().lock();
		try {
			for(RoomController table:controllers.values())
			{
				if(table.getNumber().getRoomId()!=excludeRoomId)
				{
					table.broadcast(msg);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally{
			rwLock.readLock().unlock();
		}
	}
}
