package net.jueb.fuckGame.script.game.innerScript.gate;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.base.GateController;
import net.jueb.fuckGame.game.manager.GateManager;
import net.jueb.fuckGame.script.game.innerScript.AbstractInnerScript;

public class GateUnRegScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_GateUnReg;
	}

	@Override
	public void action() {
		GateController gate=(GateController) getParams().get(0);
		if(gate!=null)
		{
			GateManager.getInstance().remove(gate.getGateInfo().getServerId());
			_log.info("网关移除:"+gate.getGateInfo());
		}
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer msg) {
		
	}
}
