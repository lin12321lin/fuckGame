package net.jueb.fuckGame.script.center;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import net.jueb.fuckGame.center.base.GateController;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.center.manager.GateManager;
import net.jueb.fuckGame.center.manager.RoleGateLockManager;
import net.jueb.fuckGame.center.mybatis.MybatisUtil;
import net.jueb.fuckGame.center.mybatis.mapper.RoleMapper;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.common.dto.RoleLock;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.core.script.AbstractServerScript;
import net.jueb.util4j.hotSwap.classFactory.IScript;
import net.jueb.util4j.net.nettyImpl.NettyConnection;
import net.jueb.util4j.net.nettyImpl.handler.LoggerHandler;

public abstract class AbstractCenterBaseScript extends AbstractServerScript{

	protected void notice_GameServerOffline(int serverId)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(serverId);
		GateManager.getInstance().broadcast(new GameMessage(getMessageCode(),buffer));
		_log.info("广播网关服务器,游戏服务器"+serverId+"离线");
	}
	
	/**
	 * 网关广播消息
	 * @param msg
	 */
	protected void broadcastGate(GameMessage msg)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeLong(msg.getCode());
		byte[] bytes=msg.getContent().getBytes();
		buffer.writeInt(bytes.length);
		buffer.writeBytes(bytes);
		GateManager.getInstance().broadcast(msg);
		_log.debug("广播所有网关消息：code="+msg.getCode());
	}

	/**
	 * 发送代理消息到网关
	 */
	protected boolean proxyRoleMsgToGate(RoleGameMessage rmsg)
	{
		int gateServerId=RoleGateLockManager.getInstance().lockedServer(rmsg.getRoleId());
		GateController gc=GateManager.getInstance().get(gateServerId);
		if(gc!=null)
		{
			proxyRoleMsgToConn(gc.getConnection(),rmsg);
		}
		return gc!=null;
	}
	
	/**
	 * 发送代理消息到连接
	 */
	protected void proxyRoleMsgToConn(NetConnection conn,RoleGameMessage rmsg)
	{
		IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_RoleGameMessage,conn,rmsg);
		script.run();
	}
	
	private RoleLock buildLock(long roleId,int serverId,LockServerType type,boolean lock)
	{
		RoleLock rl=new RoleLock();
		rl.setRoleId(roleId);
		rl.setServerId(serverId);
		rl.setType(type);
		rl.setLock(lock);
		return rl;
	}
	
	protected void broadcastRoleLockUpdate(int serverId,LockServerType type,boolean lock,long ... roleIds)
	{
		if(roleIds.length==0)
		{
			return ;
		}
		List<Long> ids=new ArrayList<>();
		for(long id:roleIds)
		{
			ids.add(id);
		}
		broadcastRoleLockUpdate(serverId, type, lock, ids);
	}
	
	protected void broadcastRoleLockUpdate(int serverId,LockServerType type,boolean lock,Collection<Long> roleIds)
	{
		if(type==null || type==LockServerType.UnDefind ||roleIds==null|| roleIds.isEmpty())
		{
			return ;
		}
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(roleIds.size());
		for(long roleId:roleIds)
		{
			RoleLock rl=buildLock(roleId, serverId, type, lock);
			rl.writeTo(buffer);
		}
		GateManager.getInstance().broadcast(new GameMessage(GameMsgCode.Center_RoleLockUpdate,buffer));
		_log.debug("广播网关角色锁更新信息:LockServerType="+type+",lock="+lock+",roles="+roleIds);
	}
	
	
	/**
	 * 修改链路日志级别
	 * @param conn
	 * @param level
	 */
	protected void changeLogLevel(NetConnection conn,LogLevel level)
	{
		if(conn instanceof NettyConnection)
		{
			try {
				NettyConnection nc=(NettyConnection)conn;
				ChannelPipeline pipe=nc.getChannel().pipeline();
				ChannelHandler ch2=pipe.get("LoggerHandler#0");
				if(ch2!=null && nc.isActive())
				{
					pipe.replace(ch2, "LoggerHandler#0", new LoggerHandler(level));
					_log.debug("修改链路日志级别完成:conn="+conn+",logLevel="+level+",pipe="+pipe.toMap().toString());
				}
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 主动刷新大厅数据
	 * @param role
	 */
	protected void roleHallDataRefresh(Role role)
	{
		IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_RoleHallDataRefresh,role.getId());
		script.run();
	}
	
	protected List<Role> saveRoles(Queue<Role> roles)
	{
		_log.info("开始同步变动角色数据"+roles.size()+"到数据库!");
		long start=System.nanoTime();
		int count=0;
		SqlSession sqlSession=null;
		List<Role> commitFailRoles=new ArrayList<>();
		 try {
			sqlSession = MybatisUtil.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);
			RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
			for(;;)
			{
				Role role=roles.poll();
				if(role==null)
				{
					break;
				}
				roleMapper.update(role);
				commitFailRoles.add(role);
				count++;
				if(count%500==0)
				{
					sqlSession.commit();
					commitFailRoles.clear();
				}
			}
			sqlSession.commit();
			commitFailRoles.clear();
		 } catch (Exception e) {
		   _log.error("角色同步发生异常,"+e.getMessage(),e);
		   if(sqlSession!=null)
		   {
			   sqlSession.rollback();
		   }
		 } finally {
			 if(sqlSession!=null)
			 {
				 sqlSession.close();
			 }
		 }
		long end=System.nanoTime();
		long useTime=TimeUnit.NANOSECONDS.toMillis(end-start);
		_log.info("角色数据同步完成,count="+count+",耗时"+useTime+"毫秒("+useTime/1000+"秒)");
		return commitFailRoles;
	}
}
