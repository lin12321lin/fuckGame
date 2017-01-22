package net.jueb.fuckGame.center;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.jctools.queues.atomic.MpmcAtomicArrayQueue;
import org.slf4j.Logger;

import io.netty.handler.logging.LogLevel;
import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.center.listener.CenterInnerListener;
import net.jueb.fuckGame.center.listener.HttpInnerListener;
import net.jueb.fuckGame.center.mybatis.MybatisUtil;
import net.jueb.fuckGame.center.mybatis.mapper.RoleMapper;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.net.handlerImpl.handler.InitalizerHttpServerHandler;
import net.jueb.fuckGame.core.net.handlerImpl.handler.InitalizerProtocolHandler;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.server.GameNettyServer;
import net.jueb.fuckGame.core.net.server.NetServer;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.util4j.cache.callBack.impl.CallBackCache;
import net.jueb.util4j.common.game.AbstractService;
import net.jueb.util4j.hotSwap.classFactory.IScript;
import net.jueb.util4j.net.nettyImpl.server.NettyServerConfig;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.QueueGroupExecutor;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultIndexQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultKeyQueueManager;
import net.jueb.util4j.queue.queueExecutor.groupExecutor.impl.DefaultQueueGroupExecutor;
import net.jueb.util4j.thread.NamedThreadFactory;

public class ServerMain extends AbstractService {
	protected Logger _log = Log4jUtil.getLogger(this.getClass());
	private static ServerMain instance;
	NettyServerConfig serverConfig=new NettyServerConfig();
	private NetServer innerServer;// 对玩家的服务端口
	private NetServer innerHttpServer;
	
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
		serverConfig.setLevel(LogLevel.TRACE);
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
		ScriptFactory.getInstance();
	}

	protected void initNetService() throws Exception {
		innerServer = new GameNettyServer(serverConfig,new InetSocketAddress(ServerConfig.BIND_HOST, ServerConfig.BIND_PORT),
				new InitalizerProtocolHandler(new CenterInnerListener()));
		innerServer.startNetWork();
		innerHttpServer=new GameNettyServer(serverConfig,new InetSocketAddress(ServerConfig.BIND_HOST, ServerConfig.BIND_HTTP_PORT),
				new InitalizerHttpServerHandler(new HttpInnerListener()));
		innerHttpServer.startNetWork();
	}
	
	/**
	 * 加载数据库角色数据和本地角色数据
	 * @throws Exception 
	 */
	public void initSqlAndLocalData() throws Exception {
		// 用户信息缓存
		_log.info("开始加载角色信息缓存……");
		SqlSession sqlSession = null;
		try {
			sqlSession = MybatisUtil.getSqlSessionFactory().openSession();
			RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
			List<Role> roles = roleMapper.findAll();
			_log.info("角色信息读取完成!,size="+roles.size());
			RoleCache.getInstance().loadRoles(roles);
			_log.info("角色信息加载到缓存完毕!");
		} catch (Exception e) {
			_log.info("角色信息加载到缓存异常:" + e.getMessage(), e);
			throw e;
		} finally {
			if(sqlSession!=null)
			{
				sqlSession.close();
			}
		}
	}

	private class DbSync implements Runnable {
		@Override
		public void run() {
			try {
				IServerScript sctipt = ScriptFactory.getInstance().buildAction(GameMsgCode.Center_RoleFlushDb);
				if (sctipt != null) {
					ServerMain.getInstance().getQueues().execute(ServerQueue.DATA_SAVE, sctipt);
				}
			} catch (Exception ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}

	private class UpdateServerInfos implements Runnable {
		@Override
		public void run() {
			try {
				IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_GameConnectionUpdate);
				ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, script);
			} catch (Exception ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}
	
	
	private class Debug implements Runnable {
		@Override
		public void run() {
			try {
				IServerScript sctipt = ScriptFactory.getInstance().buildAction(GameMsgCode.Sys_Debug);
				if (sctipt != null) {
					ServerMain.getInstance().getQueues().execute(ServerQueue.DEBUG, sctipt);
				}
			} catch (Exception ex) {
				_log.error(ex.getMessage(), ex);
			}
		}
	}
	
	
	@Override
	protected void doStart() throws Exception {
//		initSqlAndLocalData();
		initFactory();
		initNetService();
		scheduExec.scheduleWithFixedDelay(new DbSync(), 0, 1, TimeUnit.MINUTES);
		scheduExec.scheduleWithFixedDelay(new Debug(), 0, 30, TimeUnit.SECONDS);
		scheduExec.scheduleWithFixedDelay(new UpdateServerInfos(), 0, 10, TimeUnit.SECONDS);
	}

	@Override
	protected void doClose() throws Exception {
		IServerScript script = ScriptFactory.getInstance().buildAction(GameMsgCode.Sys_ServerClose);
		if (script != null) {
			script.run();
		}
	}
}
