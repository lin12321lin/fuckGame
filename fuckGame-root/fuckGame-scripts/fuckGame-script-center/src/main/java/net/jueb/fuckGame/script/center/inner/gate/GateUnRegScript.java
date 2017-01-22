package net.jueb.fuckGame.script.center.inner.gate;

import net.jueb.fuckGame.center.base.GateController;
import net.jueb.fuckGame.center.manager.GateManager;
import net.jueb.fuckGame.center.manager.RoleGateLockManager;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;

public class GateUnRegScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_GateUnReg;
	}

	@Override
	public void action() {
		super.action();
		GateController gate=(GateController) getParams().get(0);
		if(gate!=null)
		{
			GateManager.getInstance().remove(gate.getGateInfo().getServerId());
			RoleGateLockManager.getInstance().unlockByServer(gate.getGateInfo().getServerId());//解锁角色
			_log.info("网关移除:"+gate.getGateInfo());
		}
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer msg) {
		// TODO Auto-generated method stub
	}
}
