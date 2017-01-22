package net.jueb.fuckGame.script.game.role;

import java.util.ArrayList;
import java.util.List;

import net.jueb.fuckGame.core.common.enums.GameRoomEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.base.TableLocationEnum;
import net.jueb.fuckGame.game.manager.RoleManager;
import net.jueb.fuckGame.game.manager.RoomManager;

/**
 * 房主解散房间
 */
public class DisbandRoomScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_DisbandRoom;
	}
	
	@Override
	public void action() {
		RoomController room=(RoomController) getParams().get(0);
		disband(room);
	}
	
	/**
	 * 房主主动解散房间
	 */
	@Override
	protected void handleAction(NetConnection connection, RoleGameMessage action) {
		RoleController role=RoleManager.getInstance().getById(action.getRoleId());
		if(role==null)
		{
			_log.error("role not found by RoleGameMessage:"+action);
			return ;
		}
		RoomController room=role.getRoom();
		if(room==null)
		{//房间不存在
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.RoomNotFound.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		TableLocationEnum local=room.getLocation(role);//自己的位置
		if(room.getMaster()!=local)
		{//没有房主权限
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.NoPermissions.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		disband(room);
	}
	
	protected void disband(RoomController room)
	{
		RoomManager.getInstance().remove(room.getNumber().getRoomId());
		RoleController master=room.getRole(room.getMaster());
		List<RoleController> roles=new ArrayList<RoleController>();
		for(TableLocationEnum l:TableLocationEnum.values())
		{
			RoleController r=room.setRole(null,l);
			if(r!=null)
			{
				roles.add(r);
			}
		}
		//通知玩家房间已经被解散
		for(RoleController r:roles)
		{
			ByteBuffer buff=new ByteBuffer();
			buff.writeUTF(room.getNumber().toNumberString());
			r.sendMsg(new GameMessage(getMessageCode(),buff));
			r.pushChangeToCenter();
		}
		_log.debug("房间解散并移除,room="+room.getNumber());
		push_RoomEvent(room.getNumber(), GameRoomEvent.Destroy,master.getId());
	}
}