package net.jueb.fuckGame.script.game.innerScript.center;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.ServerConfig;
import net.jueb.fuckGame.game.ServerMain;

/**
 * 玩家离开游戏服
 */
public class RoleExitScript extends AbstractCenterScript{

	@Override
	public void action() {
		long roleId=(long) getParams().get(0);
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(ServerConfig.SERVER_ID);
		buffer.writeLong(roleId);
		ServerMain.getInstance().getCenterClient().sendMessage(new GameMessage(getMessageCode(), buffer));
		_log.debug("通知大厅玩家离开游戏服,roleId="+roleId);
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GameRoleExit;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		
	}
}
