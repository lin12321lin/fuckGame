package net.jueb.fuckGame.script.game.role;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.manager.RoleManager;

/**
 * 离开游戏服
 */
public class LeaveGameScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_LeaveGame;
	}
	
	/**
	 * 系统控制玩家退出
	 */
	@Override
	public void action() {
		RoleController role=(RoleController) getParams().get(0);
		RoomController room=role.getRoom();
		if(room==null)
		{//房间存在不可离开
			RoleManager.getInstance().remove(role.getId());
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_GameRoleExit,role.getId());
			script.run();
		}else
		{
			_log.error("有房间存在,不可离开游戏服,roleId="+role.getId()+",room="+room.getNumber());
		}
	}
	
	/**
	 * 玩家主动退出游戏服务器
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
		if(room!=null)
		{//房间存在不可离开
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeInt(GameErrCode.UnSupportOperation.value());
			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		RoleManager.getInstance().remove(role.getId());
		role.pushChangeToCenter();
		IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_GameRoleExit,role.getId());
		script.run();
	}
}