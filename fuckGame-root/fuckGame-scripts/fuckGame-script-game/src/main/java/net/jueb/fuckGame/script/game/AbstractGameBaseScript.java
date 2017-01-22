package net.jueb.fuckGame.script.game;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import net.jueb.fuckGame.core.common.dto.RoomNumber;
import net.jueb.fuckGame.core.common.enums.GameRoomEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.core.script.AbstractServerScript;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.ServerMain;
import net.jueb.fuckGame.game.base.GateController;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.manager.GateManager;
import net.jueb.fuckGame.game.manager.RoleManager;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 系统脚本和对外脚本公共方法
 * @author juebanlin@gmail.com
 */
public abstract class AbstractGameBaseScript extends AbstractServerScript{
	
	@Deprecated
	public byte[] readAllBytes(InputStream in)throws Exception
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
	
	public byte[] readAllBytesAndClose(InputStream in)throws Exception
	{
		byte[] data=readAllBytes(in);
		in.close();
		return data;
	}
	
	/**
	 * 发送角色游戏消息
	 * @param roleId
	 * @param msg
	 * @param conn
	 */
	protected void sendRoleGameMsg(long roleId,GameMessage msg,NetConnection conn)
	{
		proxyRoleMsgToConn(conn, new RoleGameMessage(roleId, msg));
	}
	
	/**
	 * 发送代理消息到网关
	 */
	protected boolean proxyRoleMsgToGate(RoleGameMessage rmsg)
	{
		RoleController role=RoleManager.getInstance().getById(rmsg.getRoleId());
		GateController gc=GateManager.getInstance().get(role.getGateServer());
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
	
	/**
	 * 发送游戏错误信息给玩家(玩家可能不处于当前服务器)
	 * @param conn
	 * @param roleId
	 * @param code
	 */
	protected void sendGameErrorInfo(NetConnection conn,long roleId,GameErrCode code)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(code.value());
		GameMessage rsp=new GameMessage(GameMsgCode.Game_ErrorInfo,buffer);
		proxyRoleMsgToConn(conn, new RoleGameMessage(roleId,rsp));
	}
	
	/**
	 * 发送游戏错误信息给玩家(玩家在当前服务器)
	 * @param role
	 * @param code
	 */
	protected void sendGameErrorInfo(RoleController role,GameErrCode code)
	{
		GateController g=GateManager.getInstance().get(role.getGateServer());
		if(g!=null)
		{
			sendGameErrorInfo(g.getConnection(), role.getId(), code);
		}
	}
	
	/**
	 * 通知大厅解锁,如果玩家所在当前服务器
	 * @param roleId
	 */
	protected void noticeCenterUnlockIfLock(long roleId)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(ServerConfig.SERVER_ID);
		buffer.writeLong(roleId);
		GameMessage msg=new GameMessage(GameMsgCode.Center_GameRoleUnlockIfLock,buffer);
		ServerMain.getInstance().getCenterClient().sendMessage(msg);
	}
	
	/**
	 * 推送房间事件
	 * @param roomNumber
	 * @param event
	 * @param eventRole
	 */
	protected void push_RoomEvent(RoomNumber roomNumber,GameRoomEvent event,long eventRole)
	{
		ByteBuffer buffer=new ByteBuffer();
		roomNumber.writeTo(buffer);
		buffer.writeByte(event.getValue());
		buffer.writeLong(eventRole);
		GameMessage msg=new GameMessage(GameMsgCode.Game_RoomEvent,buffer);
		ServerMain.getInstance().getCenterClient().sendMessage(msg);
		_log.debug("发送房间事件:roomNumber="+roomNumber+",event="+event+",eventRole="+eventRole);
	}
}
