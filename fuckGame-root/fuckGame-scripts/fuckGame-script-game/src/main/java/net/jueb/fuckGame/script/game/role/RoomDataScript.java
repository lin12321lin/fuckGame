package net.jueb.fuckGame.script.game.role;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.base.TableLocationEnum;
import net.jueb.fuckGame.game.base.TableStateEnum;
import net.jueb.fuckGame.game.manager.RoleManager;

/**
 * 获取房间玩家数据
 */
public class RoomDataScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoomData;
	}
	
	@Override
	public void action() {
		
	}
	
	@Override
	protected void handleAction(NetConnection connection, RoleGameMessage action) {
		RoleController role=RoleManager.getInstance().getById(action.getRoleId());
		if(role==null)
		{
			_log.error("role not found by RoleGameMessage:"+action);
			return ;
		}
		ByteBuffer buffer=new ByteBuffer();
		RoomController room=role.getRoom();
		if(room!=null)
		{
			buffer.writeBoolean(true);//是否在房间
			buffer.writeUTF(room.getNumber().toNumberString());//房间号
			buffer.writeByte(TableLocationEnum.values().length);
			//位置上玩家信息
			for(TableLocationEnum l:TableLocationEnum.values())
			{
				buffer.writeByte(l.getValue());//写入位置ID
				RoleController r=room.getRole(l);
				if(r==null)
				{
					buffer.writeBoolean(false);//没有玩家
				}else
				{
					buffer.writeBoolean(true);//有玩家
					buffer.writeUTF(r.getName());//玩家昵称
					buffer.writeUTF(r.getFaceIcon());//玩家头像
				}
			}
			TableLocationEnum local=room.getLocation(role);
			buffer.writeByte(local.getValue());//自己所在位置
			buffer.writeByte(room.getMaster().getValue());//房主位置
			buffer.writeByte(room.getState().getValue());//房间状态
			if(room.getState()==TableStateEnum.Gameing)
			{//当房间状态为游戏中时,有庄家位置信息
				buffer.writeByte(room.getBanker().getValue());//庄家位置
			}
		}else
		{
			buffer.writeBoolean(false);
		}
		responseAction(new GameMessage(getMessageCode(),buffer));
	}
}