package net.jueb.fuckGame.game.base;

import java.util.HashSet;
import java.util.Set;

public class GameRoom {

	private final int roomId;
	private final long number;//房间流水号
	private long masterId;//房主ID
	private final Set<Long> roles=new HashSet<Long>();//房间内所有玩家
	
	public GameRoom(int roomId,long number) {
		super();
		this.roomId = roomId;
		this.number=number;
	}
	public long getMasterId() {
		return masterId;
	}
	public void setMasterId(long masterId) {
		this.masterId = masterId;
	}
	
	public long getNumber() {
		return number;
	}
	public int getRoomId() {
		return roomId;
	}
	public Set<Long> getRoles() {
		return roles;
	}
	
	@Override
	public String toString() {
		return "GameRoom [roomId=" + roomId + ", number=" + number + ", masterId=" + masterId + ", roles=" + roles
				+ "]";
	}
}
