package net.jueb.fuckGame.script.game.innerScript;

import java.util.Collection;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.ServerMain;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.manager.RoomManager;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 更新所有房间
 * @author jaci
 */
public class RoomsUpdateScript extends AbstractInnerScript{

	@Override
	public void action() {
		Collection<RoomController> rooms=	RoomManager.getInstance().getList();
		for(RoomController r:rooms)
		{
			IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Game_RoomUpdate, r);
			short roomId=(short) r.getNumber().getRoomId();
			ServerMain.getInstance().getQueues().execute(roomId, script);
		}
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoomsUpdate;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}
}
