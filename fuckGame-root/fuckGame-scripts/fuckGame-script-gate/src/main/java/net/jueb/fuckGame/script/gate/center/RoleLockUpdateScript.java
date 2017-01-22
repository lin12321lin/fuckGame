package net.jueb.fuckGame.script.gate.center;

import java.util.ArrayList;
import java.util.List;

import net.jueb.fuckGame.core.common.dto.RoleLock;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.gate.ServerConfig;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;

public class RoleLockUpdateScript extends AbstractCenterScript{

	@Override
	public void action() {
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_RoleLockUpdate;
	}
	
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		List<RoleLock> locks=new ArrayList<>();
		int size=clientBuffer.readInt();
		for(int i=0;i<size;i++)
		{
			RoleLock lock=new RoleLock();
			lock.readFrom(clientBuffer);
			locks.add(lock);
		}
		if(locks.isEmpty())
		{
			return;
		}
		_log.debug("收到锁事件:"+locks);
		for(RoleLock lock:locks)
		{
			handleLock(lock);
		}
	}
	
	protected void handleLock(RoleLock lock)
	{
		RoleAgent ra=RoleAgentManager.getInstance().findAgent(lock.getRoleId());
		if(ra==null)
		{
			return ;
		}
		switch (lock.getType()) {
		case Game://游戏锁事件
			if(lock.isLock())
			{
				ra.setServerId(lock.getServerId());
			}else
			{
				ra.setServerId(0);
			}
			break;
		case Gate:
			if(lock.isLock())
			{
				if(ServerConfig.SERVER_ID!=lock.getServerId())
				{//如果是其它网关有玩家登录则踢掉当前网关玩家
					_log.debug("玩家"+lock.getRoleId()+"在其它网关服务器"+lock.getServerId()+"锁定,执行顶号处理");
					otherLoginDisconnect(ra.getRoleId(),lock.getServerId(),null,null);
				}
			}
			break;
		default:
			break;
		}
	}
}
