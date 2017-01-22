package net.jueb.fuckGame.script.center.inner.game;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.base.RoomInfo;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.core.common.dto.RoomNumber;
import net.jueb.fuckGame.core.common.enums.GameRoomEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

/**
 * 请求游戏初始化角色信息,返回成功则锁定角色所在游戏服务器
 * @author Administrator
 */
public class RoomEventScript extends AbstractInnerScript{

	@Override
	public void action() {
		
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoomEvent;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer buffer) {
		RoomNumber roomNumber=RoomNumber.valueOf(buffer);
		int serverId=roomNumber.getServerId();
		int roomId=roomNumber.getRoomId();
		GameRoomEvent event=GameRoomEvent.valueOf(buffer.readByte());
		long eventRole=buffer.readLong();
		GameController g=GameManager.getInstance().getById(serverId);
		if(g==null ||event==null)
		{
			_log.error("收到房间事件:roomId="+roomId+",event"+event+",game="+g+",eventRole="+eventRole);
			return;
		}
		switch (event) {
		case Create:
		{
			RoomInfo r=new RoomInfo(roomId);
			r.setMasterId(eventRole);
			r.getRoles().add(eventRole);
			g.getRooms().put(r.getRoomId(),r);
			_log.debug("服务器"+serverId+"创建房间事:"+r);
		}
			break;
		case Entry:
		{
			RoomInfo r=g.getRooms().get(roomId);
			if(r!=null)
			{
				r.getRoles().add(eventRole);
				_log.debug("服务器"+serverId+"加入房间事件:eventRole="+eventRole+",room="+r);
			}else
			{
				_log.error("服务器"+serverId+"加入房间事件:eventRole="+eventRole+",room="+r);
			}
		}
			break;
		case Exit:
		{
			RoomInfo r=g.getRooms().get(roomId);
			if(r!=null)
			{
				r.getRoles().remove(eventRole);
				_log.debug("服务器"+serverId+"退出房间事件:eventRole="+eventRole+",room="+r);
			}else
			{
				_log.error("服务器"+serverId+"退出房间事件:eventRole="+eventRole+",room="+r);
			}
		}
			break;
		case Destroy:
		{
			RoomInfo r=g.getRooms().remove(roomId);
			if(r!=null)
			{
				_log.debug("服务器"+serverId+"解散房间事件:eventRole="+eventRole+",room="+r);
			}else
			{
				_log.error("服务器"+serverId+"解散房间事件:eventRole="+eventRole+",room="+r);
			}
		}
			break;
		default:
			break;
		}
	}
}
