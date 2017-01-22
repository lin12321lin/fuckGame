package net.jueb.fuckGame.script.game.role;

import org.apache.commons.lang.math.RandomUtils;

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
 * 
 */
public class CreateRoomScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_CreateRoom;
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
		if(role.getRoom()!=null)
		{
			ByteBuffer buffer=new ByteBuffer();
			buffer.writeInt(GameErrCode.InOtherRoomError.value());
			responseAction(new GameMessage(getMessageCode(),buffer));
			return ;
		}
		RoomController room=RoomManager.getInstance().createRoom();
		if(room==null)
		{
			ByteBuffer buffer=new ByteBuffer();
			buffer.writeInt(GameErrCode.ServerRoomLimit.value());
			responseAction(new GameMessage(getMessageCode(),buffer));
			return ;
		}
		int rand=RandomUtils.nextInt(TableLocationEnum.values().length);
		TableLocationEnum location=TableLocationEnum.valueOf(rand);
		room.setRole(role, location);//设置玩家位置
		room.setMaster(location);//设置房主
		room.setBanker(location);//设置庄家
		//TODO 设置房间规则
		//回复房间创建完毕,以及房间相关信息
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(GameErrCode.Succeed.value());
		buffer.writeUTF(room.getNumber().toNumberString());//房间号
		buffer.writeInt(location.getValue());//房间位置
		responseAction(new GameMessage(getMessageCode(),buffer));
		push_RoomEvent(room.getNumber(), GameRoomEvent.Create, role.getId());
	}
}