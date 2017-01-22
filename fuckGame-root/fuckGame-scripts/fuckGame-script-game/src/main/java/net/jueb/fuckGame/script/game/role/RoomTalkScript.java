package net.jueb.fuckGame.script.game.role;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.game.base.RoleController;
import net.jueb.fuckGame.game.base.RoomController;
import net.jueb.fuckGame.game.base.RoomMsgTypeEnum;
import net.jueb.fuckGame.game.base.TableLocationEnum;
import net.jueb.fuckGame.game.manager.RoleManager;

/**
 * 
 */
public class RoomTalkScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Game_RoomTalk;
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
		RoomController room=role.getRoom();
		if(room==null)
		{//房间不存在
//			ByteBuffer rsp=new ByteBuffer();
//			rsp.writeInt(GameErrCode.UnSupportOperation.value());
//			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		ByteBuffer buffer=action.getMsg().getContent();
		byte type=buffer.readByte();
		RoomMsgTypeEnum typeEnum=RoomMsgTypeEnum.valueOf(type);
		if(typeEnum==null)
		{
//			ByteBuffer rsp=new ByteBuffer();
//			rsp.writeInt(GameErrCode.ArgsError.value());
//			responseAction(new GameMessage(getMessageCode(),rsp));
			return ;
		}
		TableLocationEnum local=room.getLocation(role);
		switch (typeEnum) {
		case Text:
		case Audio_Tag:
		{
			String text=buffer.readUTF();
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeByte(local.getValue());
			rsp.writeByte(type);
			rsp.writeUTF(text);
			GameMessage msg=new GameMessage(getMessageCode(),rsp);
			room.broadcast(msg);
		}
			break;
		case Binnary:
		{
			int len=buffer.readInt();
			byte[] data=buffer.readBytes(len).getBytes();
			ByteBuffer rsp=new ByteBuffer();
			rsp.writeByte(local.getValue());
			rsp.writeInt(type);
			rsp.writeInt(data.length);
			rsp.writeBytes(data);
			GameMessage msg=new GameMessage(getMessageCode(),rsp);
			room.broadcast(msg);
		}
			break;
		default:
			break;
		}
	}
}