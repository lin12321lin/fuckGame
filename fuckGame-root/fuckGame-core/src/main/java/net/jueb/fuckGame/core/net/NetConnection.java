package net.jueb.fuckGame.core.net;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 业务层与底层IO的接口
 * @author Administrator
 */
public interface  NetConnection{

	public abstract void close();
	public abstract boolean isActive();
	public abstract void sendData(byte[] data);
	public abstract void sendData(byte[] data, int offset, int count);

	public abstract void sendMessage(Object message);

	public abstract InetSocketAddress getRemote();
	
	public abstract InetSocketAddress getLocal();
	
	/**
	 * 获取远程端IP
	 * @return
	 */
	public abstract String getIP();
	
	public int getId();
	public void setId(int id);
	
	public boolean hasAttribute(String key);
	public Set<String> getAttributeNames();
	public void setAttribute(String key, Object value);
	public Object getAttribute(String key);
	public Object removeAttribute(String key);
	public void clearAttributes();
}