package net.jueb.fuckGame.core.common.dto;

import net.jueb.fuckGame.core.common.enums.RoleGateEvent;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class RoleGateEventMsg implements Dto{

	private int serverId;
	private long roleId;
	private RoleGateEvent event;
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public RoleGateEvent getEvent() {
		return event;
	}
	public void setEvent(RoleGateEvent event) {
		this.event = event;
	}
	@Override
	public String toString() {
		return "RoleGateEventMsg [serverId=" + serverId + ", roleId=" + roleId + ", event=" + event + "]";
	}
	@Override
	public void readFrom(ByteBuffer buffer) {
		serverId=buffer.readInt();
		roleId=buffer.readLong();
		event=RoleGateEvent.valueOf(buffer.readByte());
	}
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeInt(serverId);
		buffer.writeLong(roleId);
		buffer.writeByte(event.getValue());
	}
}
