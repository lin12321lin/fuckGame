package net.jueb.fuckGame.core.common.dto;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class RoleLock implements Dto {

	public static enum LockServerType {
		UnDefind(0), Game(1), Gate(2),;
		private int value;

		private LockServerType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static final LockServerType valueOf(int value) {
			for (LockServerType am : LockServerType.values())
				if (am.getValue() == value)
					return am;
			return LockServerType.UnDefind;
		}
	}

	private long roleId;
	
	private LockServerType type;
	
	private int serverId;
	
	private boolean lock;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public LockServerType getType() {
		return type;
	}

	public void setType(LockServerType type) {
		this.type = type;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	@Override
	public String toString() {
		return "RoleLock [roleId=" + roleId + ", type=" + type + ", serverId=" + serverId + ", lock=" + lock + "]";
	}

	@Override 
	public void readFrom(ByteBuffer buffer) {
		roleId=buffer.readLong();
		type=LockServerType.valueOf(buffer.readByte());
		serverId=buffer.readInt();
		lock=buffer.readBoolean();
	}

	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeLong(roleId);
		buffer.writeByte(type.getValue());
		buffer.writeInt(serverId);
		buffer.writeBoolean(lock);
	}
}
