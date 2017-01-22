package net.jueb.fuckGame.script.game.innerScript;

import java.util.concurrent.TimeUnit;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.factory.ScriptFactory;

/**
 * 更新单个房间
 * @author jaci
 */
public class RoomUpdateScript extends AbstractInnerScript{

	/**
	 * 房间最大保留秒数
	 */
	public long maxRoomAliveTime=TimeUnit.MINUTES.toMillis(10);
	
	@Override
	public void action() {
		RoomController room=(RoomController) getParams().get(0);
		switch (room.getState()) {
		case Created:
		{
			RoleController master=room.getRole(room.getMaster());
			if(!master.isOnline() && room.getRoles().size()==1)
			{//只有房主一个人的时候
				long time=System.currentTimeMillis()-master.getLastOffLineTime();
				if(time>=maxRoomAliveTime)
				{//解散房间并让房主离开游戏服务器
					ScriptFactory.getInstance().buildAction(GameMsgCode.Game_DisbandRoom, room).run();
					ScriptFactory.getInstance().buildAction(GameMsgCode.Game_LeaveGame, master).run();
				}
			}
		}
			break;

		default:
			break;
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoomUpdate;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
	}
}
