package net.jueb.fuckGame.core.net.message;

import net.jueb.fuckGame.core.util.Dto;

/**
 * 代理游戏消息
 */
public class RoleGameMessage implements Dto{

	/**
	 * 玩家ID
	 */
	private long roleId;
	/**
	 * 消息来源服务器ID
	 */
	private int serverId;
	/**
	 * 代理的玩家消息
	 */
	private GameMessage msg;
	
	public RoleGameMessage(){
		
	}
	
	public RoleGameMessage(long roleId, GameMessage msg) {
		super();
		this.roleId = roleId;
		this.msg = msg;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public GameMessage getMsg() {
		return msg;
	}
	public void setMsg(GameMessage msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return "RoleGameMessage [roleId=" + roleId + ", serverId=" + serverId + ", msg=" + msg + "]";
	}

	@Override
	public void readFrom(ByteBuffer buffer) {
		roleId=buffer.readLong();
		serverId=buffer.readInt();
		short code=buffer.readShort();
		int size=buffer.readInt();
		byte[] content=new byte[size];
		buffer.readBytes(content);
		msg=new GameMessage(code, content);
	}
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeLong(roleId);
		buffer.writeInt(serverId);
		buffer.writeShort(msg.getCode());
		byte[] data=msg.getContent().getBytes();
		buffer.writeInt(data.length);
		buffer.writeBytes(data);
	}
	
	/**
	 * 包装为一个消息
	 * @param code
	 * @return
	 */
	public GameMessage wrapGameMessage(short code)
	{
		ByteBuffer buffer=new ByteBuffer();
		this.writeTo(buffer);
		return new GameMessage(code,buffer);
	}
	
	/**
	 * 包装为一个消息
	 * @param code
	 * @return
	 */
	public GameMessage wrapGameMessage(int code)
	{
		return wrapGameMessage((short)code);
	}
}