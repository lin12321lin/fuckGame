package net.jueb.fuckGame.script.game.role;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.manager.RoleManager;

/**
 * 玩家申请解散房间
 */
public class ApplyDisbandRoomScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_ApplyDisbandRoom;
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
		//TODO 预留接口
	}
}