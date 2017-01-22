package net.jueb.fuckGame.game;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jctools.queues.atomic.MpmcAtomicArrayQueue;
import org.slf4j.Logger;

import io.netty.handler.logging.LogLevel;
import net.jueb.fuckGame.core.net.client.GameNettyClient;
import net.jueb.fuckGame.core.net.client.NetClient;
import net.jueb.fuckGame.core.net.handlerImpl.handler.InitalizerProtocolHandler;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.server.GameNettyServer;
import net.jueb.fuckGame.core.net.server.NetServer;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.listener.CenterClientListener;
import net.jueb.fuckGame.game.listener.InnerListener;
import net.jueb.util4j.cache.callBack.impl.CallBackCache;
import net.jueb.util4j.common.game.AbstractService;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultIndexQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultKeyQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.thread.NamedThreadFactory;

public class ServerMain extends AbstractService {
	protected Logger _log = Log4jUtil.getLogger(this.getClass());
	private static ServerMain instance;
	private NetServer innerServer;// 对玩家的服务端口
	private NetClient centerClient;
	private boolean isRegCenter;
	private final NettyServerConfig serverConfig=new NettyServerConfig();
	private final NettyClientConfig centerConfig=new NettyClientConfig(1);
	public static final ScheduledThreadPoolExecutor scheduExec = new ScheduledThreadPoolExecutor(2,new NamedThreadFactory("taskTimer", true));
	public final QueueGroupExecutor queues;
	private final CallBackCache callBackCache;
	
	{
		int maxQueueSize=Short.MAX_VALUE;
		DefaultIndexQueueManager iqm=new DefaultIndexQueueManager.Builder().setMpScQueueFactory().build();
		DefaultKeyQueueManager kqm=new DefaultKeyQueueManager.Builder().setMpScQueueFactory().build();
		queues=new DefaultQueueGroupExecutor.Builder()
				.setBossQueue(new MpmcAtomicArrayQueue<Runnable>(maxQueueSize))
				.setCorePoolSize(2)
				.setMaxPoolSize(20)
				.setThreadFactory(new NamedThreadFactory("queueGroup",true))
				.setIndexQueueGroupManager(iqm)
				.setKeyQueueGroupManagerr(kqm)
				.build();
		callBackCache=new CallBackCache(queues.getQueueExecutor(ServerQueue.MAIN));
		//设置底层日志级别,方便调试
		serverConfig.setLevel(LogLevel.TRACE);
		centerConfig.setLevel(LogLevel.TRACE);
		scheduExec.scheduleWithFixedDelay(callBackCache.getCleanTask(), 0, 1, TimeUnit.SECONDS);
	}
	
	public static ServerMain getInstance() {
		if (instance == null) {
			instance = new ServerMain();
		}
		return instance;
	}

	public QueueGroupExecutor getQueues() {
		return queues;
	}

	public CallBackCache getCallBackCache() {
		return callBackCache;
	}

	protected void initFactory() {
		
	}

	public boolean isRegCenter() {
		return isRegCenter;
	}

	public void setRegCenter(boolean isRegCenter) {
		this.isRegCenter = isRegCenter;
	}

	public NetClient getCenterClient() {
		return centerClient;
	}

	public void setCenterClient(NetClient centerClient) {
		this.centerClient = centerClient;
	}

	protected void initNetService() throws Exception {
		centerClient=new GameNettyClient(centerConfig,ServerConfig.CENTER_HOST,ServerConfig.CENTER_PORT, 
				new InitalizerProtocolHandler(new CenterClientListener()));
	    centerClient.enableReconnect(scheduExec,TimeUnit.SECONDS.toMillis(5));
		centerClient.setName("centerClient");
		centerClient.startNetWork();
		
		innerServer = new GameNettyServer(serverConfig,ServerConfig.BIND_HOST, ServerConfig.BIND_PORT,
				new InitalizerProtocolHandler(new InnerListener()));
		innerServer.startNetWork();
	}

	
	private class Debug implements Runnable {
		@Override
		public void run() {
			try {
				IServerScript sctipt = ScriptFactory.getInstance()
						.buildAction(GameMsgCode.Sys_Debug);
				if (sctipt != null) {
					ServerMain.getInstance().getQueues().execute(ServerQueue.DEBUG, sctipt);
				}
			} catch (Exception ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}
	
	private class RoomsUpdate implements Runnable {
		@Override
		public void run() {
			try {
				IServerScript sctipt = ScriptFactory.getInstance().buildAction(GameMsgCode.Game_RoomsUpdate);
				if (sctipt != null) {
					ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, sctipt);
				}
			} catch (Exception ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}
	
	@Override
	protected void doStart() throws Exception {
		initFactory();
		initNetService();
		// 10秒执行一次日志缓存数据到日志服务器的任务
		scheduExec.scheduleWithFixedDelay(new Debug(), 0, 30, TimeUnit.SECONDS);
		scheduExec.scheduleWithFixedDelay(new RoomsUpdate(), 0, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void doClose() throws Exception {
		
	}
}
