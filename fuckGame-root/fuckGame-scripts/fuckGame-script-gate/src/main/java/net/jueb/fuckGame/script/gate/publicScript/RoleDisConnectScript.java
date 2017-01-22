package net.jueb.fuckGame.script.gate.publicScript;

import net.jueb.fuckGame.core.common.enums.RoleGateEvent;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.factory.ScriptFactory;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;
import net.jueb.util4j.cache.map.TimedMap.EventListener;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 角色断线
 * @author Administrator
 */
public class RoleDisConnectScript extends AbstractPublicScript implements EventListener<Long,RoleAgent>{

	@Override
	public void action() {
		RoleAgent ra=(RoleAgent) getParams().get(0);
		ra.setLastOffTime(System.currentTimeMillis());
		NetConnection offlineConn=ra.getConnection();
		ra.setConnection(null);
		RoleAgentManager.getInstance().addListener(ra.getRoleId(), this);
		RoleAgentManager.getInstance().updateTTL(ra.getRoleId(), RoleAgent.RECONNECT_TIME);
		RoleAgent.totalDisConnect.incrementAndGet();
		_log.debug("网关角色掉线,等待重连,offlineConn="+offlineConn+"ra="+ra);
		roleGateEvent(ra,RoleGateEvent.Disconnect);
	}
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_RoleDisConnect;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		// TODO Auto-generated method stub
	}

	/**
	 * 当RoleAgent被移除后执行此方法
	 */
	@Override
	public void removed(Long key, RoleAgent value,boolean expire) {
		_log.debug("网关角色掉线,重连超时被移除:"+value);
		IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_LoginOut, value);
		ServerMain.getInstance().getQueues().execute(ServerQueue.Login, script);
	}
}
