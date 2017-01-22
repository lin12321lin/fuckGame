package net.jueb.fuckGame.script.game.innerScript.center;

import net.jueb.fuckGame.core.common.dto.GameEntryInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.manager.RoleManager;

public class RoleEntryScript extends AbstractCenterScript{

	@Override
	public void action() {
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoleEntry;
	}

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		String callKey=clientBuffer.readUTF();
		GameEntryInfo info=new GameEntryInfo();
		info.readFrom(clientBuffer);
		long roleId=info.getRoleId();
		RoleController role=RoleManager.getInstance().getById(roleId);
		if(role==null)
		{//创建角色
			role=new RoleController(info.getRoleId());
			role.setMoney(info.getMoney());
			role.setBag(info.getBag());
			RoleManager.getInstance().add(role);
		}
		//已经存在的用户角色不更新金币物品数据
		role.setFaceIcon(info.getFaceIcon());
		role.setGateServer(info.getGateId());
		role.setIp(info.getIp());
		role.setName(info.getName());
		role.setOnline(true);
		ByteBuffer rsp=new ByteBuffer();
		rsp.writeUTF(callKey);
		rsp.writeInt(GameErrCode.Succeed.value());
		connection.sendMessage(new GameMessage(getMessageCode(),rsp));
		_log.debug("玩家进入游戏,info="+info);
	}
}
