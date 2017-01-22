package net.jueb.fuckGame.center.base;

import java.util.HashSet;
import java.util.Set;

/**
 * //通知玩家创建好的房间ID，type，随机码  40030
message RoomInfoUpdate_GL
{
	required int32 PlayerID = 1;
	required bool Owner	= 2;
	required int32 Number = 3;
	required int32 ServerID = 4;
	required int32 RoomID = 5;
	optional int32 type = 6;
	optional int32 RandomCode = 7;
}
 * @author Administrator
 *
 */
public class RoomInfo {

	private final int roomId;
	private long masterId;//房主ID
	
	public RoomInfo(int roomId) {
		super();
		this.roomId = roomId;
	}
	private final Set<Long> roles=new HashSet<Long>();//房间内所有玩家
	
	public long getMasterId() {
		return masterId;
	}
	public void setMasterId(long masterId) {
		this.masterId = masterId;
	}
	public int getRoomId() {
		return roomId;
	}
	public Set<Long> getRoles() {
		return roles;
	}
	@Override
	public String toString() {
		return "RoomInfo [roomId=" + roomId + ", masterId=" + masterId + ", roles=" + roles + "]";
	}
}
