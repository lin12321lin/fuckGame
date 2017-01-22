package net.jueb.fuckGame.core.net.client;

import java.util.concurrent.ScheduledExecutorService;

import net.jueb.fuckGame.core.net.message.GameMessage;

public interface NetClient{

	public abstract void startNetWork();
	
	public abstract void close();

	public abstract void sendMessage(GameMessage msg);

	public abstract void sendData(byte[] data, int offset, int count);

	public abstract boolean isActive();
	
	public abstract void setName(String name);
	
	/**
	 * 开启断线重连
	 * @param executor 断线重连调度服务器
	 * @param timeMills 间隔
	 */
	public void enableReconnect(ScheduledExecutorService executor,long timeMills);
	
	/**
	 * 禁用断线重连
	 * @param reconnect
	 */
	public void disableReconnect();
	
	public boolean isReconnect();
}
