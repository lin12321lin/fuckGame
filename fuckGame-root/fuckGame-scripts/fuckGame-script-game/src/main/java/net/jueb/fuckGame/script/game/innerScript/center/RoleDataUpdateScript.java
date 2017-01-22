package net.jueb.fuckGame.script.game.innerScript.center;

import net.jueb.fuckGame.core.common.dto.RoleChange;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.ServerMain;

public class RoleDataUpdateScript extends AbstractCenterScript{

	@Override
	public void action() {
		RoleChange change=(RoleChange) getParams().get(0);
		ByteBuffer buff=new ByteBuffer();
		change.writeTo(buff);
		ServerMain.getInstance().getCenterClient().sendMessage(new GameMessage(getMessageCode(),buff));
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoleDataUpdate;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		
	}
}
