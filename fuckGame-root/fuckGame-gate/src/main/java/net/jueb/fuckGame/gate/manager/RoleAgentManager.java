package net.jueb.fuckGame.gate.manager;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.util4j.cache.map.TimedMap;
import net.jueb.util4j.cache.map.TimedMap.EventListener;
import net.jueb.util4j.cache.map.TimedMapImpl;

public class RoleAgentManager {

	private static RoleAgentManager instance;
	private static final Object lock_0=new Object();
	private final TimedMap<Long,RoleAgent> agents=new TimedMapImpl<Long,RoleAgent>(ServerMain.getInstance().getQueues().getQueueExecutor(ServerQueue.MAIN));
	
	private RoleAgentManager() {
		Runnable cleanTask=agents.getCleanTask();
		ServerMain.scheduExec.scheduleAtFixedRate(cleanTask,10,1,TimeUnit.SECONDS);
	}
	
	public static RoleAgentManager getInstance()
	{
		if(instance==null)
		{
			synchronized (lock_0) {
				if(instance==null)
				{
					instance=new RoleAgentManager();
				}
			}
		}
		return instance;
	}
	
	public void addAgent(RoleAgent agent)
	{
		if(agent==null)
		{
			return;
		}
		agents.put(agent.getRoleId(), agent);
	}
	
	public void addAgent(RoleAgent agent,long TTL)
	{
		if(agent==null)
		{
			return;
		}
		agents.put(agent.getRoleId(), agent,TTL);
	}
	
	public Collection<RoleAgent> values()
	{
		return agents.values();
	}
	
	public RoleAgent findAgent(long roleId)
	{
		return agents.get(roleId);
	}
	
	public RoleAgent updateTTL(long roleId,long ttl)
	{
		return agents.updateTTL(roleId, ttl);
	}
	
	public RoleAgent addListener(long roleId,EventListener<Long,RoleAgent> listener)
	{
		return agents.addEventListener(roleId, listener);
	}
	
	public RoleAgent removeAllListener(long roleId)
	{
		return agents.removeAllEventListener(roleId);
	}
	
	public RoleAgent findAgentByReToken(String retoken)
	{
		for(RoleAgent ra:agents.values())
		{
			if(ra.getConnection()==null && StringUtils.equals(ra.getReToken(),retoken))
			{
				return ra;
			}
		}
		return null;
	}
	
	public RoleAgent remove(long roleId)
	{
		return agents.remove(roleId);
	}
	
	/**
	 * 广播消息
	 * @param msg
	 */
	public void broadcast(GameMessage msg)
	{
		for(RoleAgent agent:agents.values())
		{
			agent.sendMessage(msg);
		}
	}
	
	/**
	 * 广播消息
	 * @param msg
	 */
	public void broadcast(GameMessage msg,RoleAgent agent)
	{
		for(RoleAgent a:agents.values())
		{
			if(a!=agent)
			{
				agent.sendMessage(msg);
			}
		}
	}
}
