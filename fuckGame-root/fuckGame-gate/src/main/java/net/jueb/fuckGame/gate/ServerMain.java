package net.jueb.fuckGame.gate;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jctools.queues.atomic.MpmcAtomicArrayQueue;
import org.slf4j.Logger;

import io.netty.handler.logging.LogLevel;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.client.GameNettyClient;
import net.jueb.fuckGame.core.net.client.NetClient;
import net.jueb.fuckGame.core.net.handlerImpl.handler.InitalizerProtocolHandler;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.server.GameNettyServer;
import net.jueb.fuckGame.core.net.server.NetServer;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.gate.factory.ScriptFactory;
import net.jueb.fuckGame.gate.listener.CenterClientListener;
import net.jueb.fuckGame.gate.listener.PublicListener;
import net.jueb.util4j.cache.callBack.impl.CallBackCache;
import net.jueb.util4j.common.game.AbstractService;
import net.jueb.util4j.net.nettyImpl.client.NettyClientConfig;
import net.jueb.util4j.net.nettyImpl.client.connections.ConnectionBuilder;
import net.jueb.util4j.net.nettyImpl.handler.websocket.binary.WebSocketServerBinaryAdapterHandler;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultIndexQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultKeyQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.thread.NamedThreadFactory;

public class ServerMain extends AbstractService {
	protected Logger _log = Log4jUtil.getLogger(this.getClass());
	private static ServerMain instance;
	private NetServer publicServer;// 对玩家的服务端口
	private NetClient centerClient;
	private final ConnectionBuilder gameClientConnector=new ConnectionBuilder(4);
	private final NettyClientConfig centerClientConfig=new NettyClientConfig(1);
	private final NettyServerConfig serverConfig=new NettyServerConfig();
	public static final ScheduledThreadPoolExecutor scheduExec = new ScheduledThreadPoolExecutor(2,new NamedThreadFactory("ServerExecutor", true));
	private boolean isRegCenter;
	private final Set<NetConnection> publicClients=new HashSet<NetConnection>();
	private final AtomicInteger onlineConnCount=new AtomicInteger();
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
		gameClientConnector.setLevel(LogLevel.TRACE);
		centerClientConfig.setLevel(LogLevel.TRACE);
		serverConfig.setLevel(LogLevel.TRACE);
		scheduExec.scheduleWithFixedDelay(callBackCache.getCleanTask(), 0, 1, TimeUnit.SECONDS);
	}

	public ConnectionBuilder getGameClientConnector() {
		return gameClientConnector;
	}

	public NettyServerConfig getServerConfig() {
		return serverConfig;
	}

	public QueueGroupExecutor getQueues() {
		return queues;
	}

	public CallBackCache getCallBackCache() {
		return callBackCache;
	}

	public Set<NetConnection> getPublicClients() {
		return publicClients;
	}

	public static ServerMain getInstance() {
		if (instance == null) {
			instance = new ServerMain();
		}
		return instance;
	}

	public NetClient getCenterClient() {
		return centerClient;
	}

	public boolean isRegCenter() {
		return isRegCenter;
	}

	public void setRegCenter(boolean isRegCenter) {
		this.isRegCenter = isRegCenter;
	}

	public AtomicInteger getOnlineConnCount() {
		return onlineConnCount;
	}

	protected void initNetService() throws Exception {
		centerClient=new GameNettyClient(centerClientConfig,ServerConfig.CENTER_HOST,ServerConfig.CENTER_PORT, 
				new InitalizerProtocolHandler(new CenterClientListener()));
	    centerClient.enableReconnect(scheduExec,TimeUnit.SECONDS.toMillis(5));
		centerClient.setName("centerClient");
		centerClient.startNetWork();
		
		publicServer = new GameNettyServer(serverConfig,ServerConfig.BIND_HOST, ServerConfig.BIND_PORT,
				new InitalizerProtocolHandler(new PublicListener()));
		publicServer.startNetWork();
		//websockt端口
		publicServer = new GameNettyServer(serverConfig,ServerConfig.BIND_HOST, ServerConfig.BIND_PORT+1,
				new WebSocketServerBinaryAdapterHandler("/",new InitalizerProtocolHandler(new PublicListener())));
		publicServer.startNetWork();
	}
	
	private class Debug implements Runnable {
		@Override
		public void run() {
			try {
				IServerScript script = ScriptFactory.getInstance().buildAction(GameMsgCode.Sys_Debug);
				if (script != null) {
					ServerMain.getInstance().getQueues().execute(ServerQueue.DEBUG, script);
				}
			} catch (Exception ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}
	
	@Override
	protected void doStart() throws Exception {
		initNetService();
		// 10秒执行一次日志缓存数据到日志服务器的任务
		scheduExec.scheduleWithFixedDelay(new Debug(), 0, 30, TimeUnit.SECONDS);
	}

	@Override
	protected void doClose() throws Exception {
		
	}
}
