package net.jueb.fuckGame.core.script;

import java.util.List;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 服务器脚本
 * @author juebanlin@gmail.com
 * time:2015年6月17日
 */
public interface IServerScript extends IScript{

	public void setRequest(Request request);
	
	public Request getRequest();
	
	public void setRunMode(RunMode mode);
	
	public RunMode getRunMode();
	
	public List<Object> getParams();
	
	@SuppressWarnings("unchecked")
	default public <T> T getParam(int index) {
		return (T) getParams().get(index);
	}
	/**
	 * 如果不存在此索引对象则返回arg
	 * @param index
	 * @param arg
	 * @return
	 */
	default public <T> T getParamOrElse(int index,T arg)
	{
		T t=null;
		if(index>=0 && index<getParams().size())
		{
			t=getParam(index);
		}else
		{
			t=arg;
		}
		return t;
	}
	/**
	 * 如果不存在此索引对象则返回null
	 * @param index
	 * @return
	 */
	default public <T> T getParamOrNull(int index)
	{
		return getParamOrElse(index, null);
	}
	
	public void setParams(Object ...params);
	
	public void action();
	
	public void handleRequest(Request request);
	
	public static enum RunMode{
		/**
		 * 执行操作
		 */
		Action,
		/**
		 * 执行请求处理
		 */
		HandleRequest;
	}
	
	public static class Request {
		private NetConnection connection;
		private Object content;

		public Request() {
			
		}

		public Request(NetConnection connection, Object content) {
			super();
			this.connection = connection;
			this.content = content;
		}

		public NetConnection getConnection() {
			return connection;
		}

		public void setConnection(NetConnection connection) {
			this.connection = connection;
		}

		public Object getContent() {
			return content;
		}

		public void setContent(Object content) {
			this.content = content;
		}
	}
}
