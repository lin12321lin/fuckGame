package net.jueb.fuckGame.script.center.inner.game;

import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.core.common.dto.GameEntryInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;
import net.jueb.util4j.cache.callBack.CallBack;

/**
 * 请求游戏初始化角色信息,返回成功则锁定角色所在游戏服务器
 * @author Administrator
 */
public class RoleEntrytScript extends AbstractInnerScript{

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
		NetConnection conn=getParam(0);
		GameEntryInfo info=getParam(1);
		CallBack<ByteBuffer> callBack=(CallBack<ByteBuffer>) getParams().get(1);
		long callKey=ServerMain.getInstance().getCallBackCache().put(callBack);
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeLong(callKey);
		info.writeTo(buffer);
		conn.sendMessage(new GameMessage(getMessageCode(),buffer));
		_log.debug("游戏服进入消息发送:roleId="+info.getRoleId()+",callKey="+callKey+","+"\n");
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoleEntry;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		long callKey=buffer.readLong();
		CallBack<ByteBuffer> callBack=ServerMain.getInstance().getCallBackCache().poll(buffer, callKey);
		_log.debug("游戏服进入消息返回,callKey="+callKey+",callBack="+callBack);
		if(callBack!=null)
		{
			callBack.call(buffer);
		}
	}
}
