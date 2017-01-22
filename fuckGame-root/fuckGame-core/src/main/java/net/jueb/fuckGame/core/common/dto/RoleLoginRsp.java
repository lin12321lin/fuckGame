package net.jueb.fuckGame.core.common.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jueb.fuckGame.core.common.enums.RoleSexEnum;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class RoleLoginRsp implements Dto{

	private long roleId;
	private String name;
	private long money;
	private String faceIcon;
	private RoleSexEnum sex;
	/**
	 * 物品
	 */
	private Map<Integer,Integer> bagItem=new HashMap<Integer,Integer>();
	private int gameServerId;
	
	
	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}
	public RoleSexEnum getSex() {
		return sex;
	}

	public void setSex(RoleSexEnum sex) {
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public String getFaceIcon() {
		return faceIcon;
	}

	public void setFaceIcon(String faceIcon) {
		this.faceIcon = faceIcon;
	}

	public int getGameServerId() {
		return gameServerId;
	}

	public void setGameServerId(int gameServerId) {
		this.gameServerId = gameServerId;
	}

	public Map<Integer, Integer> getBagItem() {
		return bagItem;
	}

	public void setBagItem(Map<Integer, Integer> bagItem) {
		this.bagItem = bagItem;
	}

	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeLong(roleId);
		buffer.writeUTF(name);
		buffer.writeByte(sex.getValue());
		buffer.writeLong(money);
		buffer.writeUTF(faceIcon);
		buffer.writeInt(gameServerId);
		int size=bagItem.size();
		buffer.writeInt(size);
		for(Entry<Integer, Integer> e:bagItem.entrySet())
		{
			buffer.writeInt(e.getKey());
			buffer.writeInt(e.getValue());
		}
	}

	@Override
	public void readFrom(ByteBuffer buffer) {
		roleId=buffer.readLong();
		name=buffer.readUTF();
		sex=RoleSexEnum.valueOf(buffer.readByte());
		money=buffer.readLong();
		faceIcon=buffer.readUTF();
		gameServerId=buffer.readInt();
		Map<Integer,Integer> item=new HashMap<>();
		int size=buffer.readInt();
		for(int i=0;i<size;i++)
		{
			item.put(buffer.readInt(),buffer.readInt());
		}
		bagItem=item;
	}

	@Override
	public String toString() {
		return "RoleLoginRsp [roleId=" + roleId + ", name=" + name + ", money=" + money + ", faceIcon=" + faceIcon
				+ ", sex=" + sex + ", bagItem=" + bagItem + ", gameServerId=" + gameServerId + "]";
	}
}
