package net.jueb.fuckGame.script.game.role;

import net.jueb.fuckGame.core.common.dto.RoomNumber;
import net.jueb.fuckGame.core.common.enums.GameRoomEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.base.TableLocationEnum;
import net.jueb.fuckGame.game.base.TableStateEnum;
import net.jueb.fuckGame.game.manager.RoleManager;
import net.jueb.fuckGame.game.manager.RoomManager;

/**
 * 
 */
public class EntryRoomScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_EntryRoom;
	}
	
	@Override
	public void action() {
		
	}
	
	@Override
	protected void handleAction(NetConnection connection, RoleGameMessage action) {
		RoleController role=RoleManager.getInstance().getById(action.getRoleId());
		if(role==null)
		{
			_log.error("role not found by RoleGameMessage:"+action);
			return ;
		}
		ByteBuffer buff=action.getMsg().getContent();
		int roomNumber=buff.readInt();
		RoomNumber num=RoomNumber.valueOf(roomNumber);
		RoomController myroom=role.getRoom();
		if(myroom!=null)
		{//已经存在某房间不能加入
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.InOtherRoomError.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		if(num.getServerId()!=ServerConfig.SERVER_ID)
		{//房间号所属服务器错误
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.RoomNumberError.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		RoomController room=RoomManager.getInstance().getById(num.getRoomId());
		if(room==null)
		{//房间不存在
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.RoomNotFound.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		if(room.getState()!=TableStateEnum.Created)
		{//房间人数已满
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.RoomRoleLimit.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		TableLocationEnum local=null;
		for(TableLocationEnum l:TableLocationEnum.values())
		{
			if(room.getRole(l)==null)
			{//选择一个空位置
				local=l;break;
			}
		}
		if(local==null)
		{
			_log.error("玩家无法找到可用位置,reqnum:"+num+",room="+room);
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.RoomRoleLimit.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		room.setRole(role,local);//设置桌子
		ByteBuffer rsp=new ByteBuffer();
		rsp.writeInt(GameErrCode.Succeed.value());
		rsp.writeUTF(room.getNumber().toNumberString());//房间号
		rsp.writeInt(local.getValue());//自己位置信息
		responseAction(new GameMessage(getMessageCode(),rsp));
		push_RoomEvent(room.getNumber(), GameRoomEvent.Entry, role.getId());
	}
}