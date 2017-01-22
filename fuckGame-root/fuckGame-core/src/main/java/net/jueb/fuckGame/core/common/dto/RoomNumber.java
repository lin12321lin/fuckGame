package net.jueb.fuckGame.core.common.dto;

import java.text.DecimalFormat;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class RoomNumber implements Dto{
	final static DecimalFormat df2 = new DecimalFormat("00");
	final static DecimalFormat df4 = new DecimalFormat("0000");
	final static DecimalFormat df6 = new DecimalFormat("000000");
	private int serverId;// 服务器ID
	private int roomId;// 房间ID

	static {
		df2.setMaximumIntegerDigits(2);
		df4.setMaximumIntegerDigits(4);
		df6.setMaximumIntegerDigits(6);
	}

	RoomNumber() {
	}

	public int getServerId() {
		return serverId;
	}

	void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getRoomId() {
		return roomId;
	}

	void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String toNumberString()
	{
		return df2.format(serverId)+df4.format(roomId);
	}
	
	public int toNumber()
	{
		return Integer.valueOf(toNumberString());
	}
	
	@Override
	public String toString() {
		return "RoomNumber [serverId=" + df2.format(serverId) + ", roomId=" + df4.format(roomId) + ", toNumber=" + toNumber()+", toNumberString=" + toNumberString()+"]";
	}

	@Override
	public void readFrom(ByteBuffer buffer) {
		serverId=buffer.readInt();
		roomId=buffer.readInt();
	}

	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeInt(serverId);
		buffer.writeInt(roomId);
	}

	public static RoomNumber valueOf(int serverId, int roomId) {
		String roomNumber = df2.format(serverId) + df4.format(roomId);
		return valueOf(roomNumber);
	}

	public static RoomNumber valueOf(int roomNumber) {
		RoomNumber num = new RoomNumber();
		String str = df6.format(roomNumber);
		int serverId = Integer.parseInt(str.substring(0, 2));
		int roomId = Integer.parseInt(str.substring(2, 6));
		num.setServerId(serverId);
		num.setRoomId(roomId);
		return num;
	}

	public static RoomNumber valueOf(String roomNumber) {
		return valueOf(Integer.valueOf(roomNumber));
	}

	public static RoomNumber valueOf(ByteBuffer buffer) {
		RoomNumber rom=new RoomNumber();
		rom.readFrom(buffer);
		return rom;
	}
}