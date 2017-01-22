package net.jueb.fuckGame.script.gate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.jueb.fuckGame.core.common.dto.RoleGateEventMsg;
import net.jueb.fuckGame.core.common.enums.RoleGateEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.client.NetClient;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.core.script.AbstractServerScript;
import net.jueb.fuckGame.gate.ServerConfig;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.manager.GameConnectionManager;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;

/**
 * 系统脚本和对外脚本公共方法
 * @author juebanlin@gmail.com
 */
public abstract class AbstractGateBaseScript extends AbstractServerScript{
	
	@Deprecated
	protected byte[] readAllBytes(InputStream in)throws Exception
	{
		byte[] data=new byte[]{};
		if(in!=null)
		{
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
	        byte[] buffer = new byte[1024];  
	        int len = -1;  
	        while ((len = in.read(buffer)) != -1) {  
	            outSteam.write(buffer, 0, len);  
	        }  
	        outSteam.close();  
	        data=outSteam.toByteArray();  
		}
		return data;
	}
	
	protected byte[] readAllBytesAndClose(InputStream in)throws Exception
	{
		byte[] data=readAllBytes(in);
		in.close();
		return data;
	}
	
	/**
	 *  发送错误信息
	 * @param connection
	 * @param errCode
	 */
	protected void sendGateError(NetConnection connection,GameErrCode errCode)
	{
		if(connection!=null)
		{
			ByteBuffer buffer=new ByteBuffer();
			buffer.writeInt(errCode.value());
			connection.sendMessage(new GameMessage(GameMsgCode.Gate_ErrorInfo,buffer));
		}
	}
	
	/**
	 * 包装为消息
	 * @param roleId
	 * @param msg
	 * @return
	 */
	protected GameMessage buildRoleGameMessage(long roleId,GameMessage msg)
	{
		RoleGameMessage rmsg=new RoleGameMessage();
		rmsg.setMsg(msg);
		rmsg.setRoleId(roleId);
		rmsg.setServerId(ServerConfig.SERVER_ID);
		return rmsg.wrapGameMessage(GameMsgCode.Gate_RoleGameMessage);
	}
	
	/**
	 * 发送代理消息到链接
	 */
	protected boolean proxyRoleMsgToConn(NetConnection conn,long roleId,GameMessage msg)
	{
		conn.sendMessage(buildRoleGameMessage(roleId,msg));
		return conn.isActive();
	}
	
	/**
	 * 发送代理消息到大厅
	 * @param conn
	 * @param rmsg
	 * @return
	 */
	protected void proxyRoleMsgToCenter(long roleId,GameMessage msg)
	{
		NetClient client=ServerMain.getInstance().getCenterClient();
		client.sendMessage(buildRoleGameMessage(roleId,msg));
	}
	
	/**
	 * 发送角色网关事件到大厅和游戏
	 * @param ra
	 * @param event
	 */
	protected void roleGateEvent(RoleAgent ra,RoleGateEvent event)
	{
		RoleGateEventMsg msg=new RoleGateEventMsg();
		msg.setEvent(event);
		msg.setServerId(ServerConfig.SERVER_ID);
		msg.setRoleId(ra.getRoleId());
		//发送给游戏
		NetConnection conn=GameConnectionManager.getInstance().getServerConnection(ra.getServerId());
		if(conn!=null && conn.isActive())
		{
			ByteBuffer buffer=new ByteBuffer();
			msg.writeTo(buffer);
			conn.sendMessage(new GameMessage(GameMsgCode.Gate_RoleGateEvent, buffer));
		}
		ByteBuffer buffer=new ByteBuffer();
		msg.writeTo(buffer);
		//发送给大厅
		ServerMain.getInstance().getCenterClient().sendMessage(new GameMessage(GameMsgCode.Gate_RoleGateEvent, buffer));
	}
	
	/**
	 * 顶号处理
	 */
	protected void otherLoginDisconnect(long roleId,int newGateId,NetConnection oldConn,NetConnection newConn)
	{
		RoleAgentManager.getInstance().removeAllListener(roleId);
		RoleAgent ra=RoleAgentManager.getInstance().remove(roleId);
		if(oldConn!=null)
		{
			oldConn.sendMessage(new GameMessage(GameMsgCode.Gate_OtherLogin));
			oldConn.clearAttributes();
			oldConn.close();
			_log.debug("顶号处理:ra="+ra+",newGateId="+newGateId+",newConn="+newConn+",关闭旧链路:conn="+oldConn);
		}
	}
}
