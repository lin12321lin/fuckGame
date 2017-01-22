package net.jueb.fuckGame.script.game.role;

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
import net.jueb.fuckGame.game.base.TableStateEnum;
import net.jueb.fuckGame.game.manager.RoleManager;

/**
 * 
 */
public class LeaveRoomScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_LeaveRoom;
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
		RoomController room=role.getRoom();
		if(room==null)
		{//房间不存在
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.UnSupportOperation.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		if(!room.hasRole(role))
		{//不在此房间
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.UnSupportOperation.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		RoleController master=room.getRole(room.getMaster());
		if(master==role)
		{//房主不能离开房间,只能解散房间
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.UnSupportOperation.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		if(room.getState()!=TableStateEnum.Created)
		{//房间此时不可离开
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.UnSupportOperation.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		TableLocationEnum local=room.getLocation(role);
		RoleController old=room.setRole(null, local);
		if(old!=role)
		{//意外情况
			_log.error("玩家离开房间异常,self:"+role+",local="+local+",old="+old+",room="+room);
			room.setRole(old, local);//还原
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.UnknownError.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		ByteBuffer rsp=new ByteBuffer();
		rsp.writeInt(GameErrCode.Succeed.value());
		responseAction(new GameMessage(getMessageCode(),rsp));
		push_RoomEvent(room.getNumber(), GameRoomEvent.Exit, role.getId());
	}
}