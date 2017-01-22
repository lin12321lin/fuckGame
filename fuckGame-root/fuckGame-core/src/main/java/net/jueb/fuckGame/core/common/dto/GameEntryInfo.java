package net.jueb.fuckGame.core.common.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class GameEntryInfo implements Dto{

	private long roleId;
	private String name;
	private String faceIcon;
	private long money;
	private Map<Integer,Integer> bag;
	private String ip;
	/**
	 * 所在网关ID
	 */
	private int gateId;
	/**
	 * 是否使用房间号匹配进入的
	 */
	private int roomNumber;
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFaceIcon() {
		return faceIcon;
	}
	public void setFaceIcon(String faceIcon) {
		this.faceIcon = faceIcon;
	}
	public long getMoney() {
		return money;
	}
	public void setMoney(long money) {
		this.money = money;
	}
	public Map<Integer, Integer> getBag() {
		return bag;
	}
	public void setBag(Map<Integer, Integer> bag) {
		this.bag = bag;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getGateId() {
		return gateId;
	}
	public void setGateId(int gateId) {
		this.gateId = gateId;
	}
	
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	@Override
	public void readFrom(ByteBuffer buffer) {
		roleId=buffer.readLong();
		name=buffer.readUTF();
		faceIcon=buffer.readUTF();
		money=buffer.readLong();
		int size=buffer.readInt();
		bag=new HashMap<>();
		for(int i=0;i<size;i++)
		{
			bag.put(buffer.readInt(), buffer.readInt());
		}
		ip=buffer.readUTF();
		gateId=buffer.readInt();
		roomNumber=buffer.readInt();
	}
	
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeLong(roleId);
		buffer.writeUTF(name);
		buffer.writeUTF(faceIcon);
		buffer.writeLong(money);
		buffer.writeInt(bag.size());
		for(Entry<Integer, Integer> e:bag.entrySet())
		{
			buffer.writeInt(e.getKey());
			buffer.writeInt(e.getValue());
		}
		buffer.writeUTF(ip);
		buffer.writeInt(gateId);
		buffer.writeInt(roomNumber);
	}
	@Override
	public String toString() {
		return "GameEntryInfo [roleId=" + roleId + ", name=" + name + ", faceIcon=" + faceIcon + ", money=" + money
				+ ", bag=" + bag + ", ip=" + ip + ", gateId=" + gateId + ", roomNumber=" + roomNumber + "]";
	}
}