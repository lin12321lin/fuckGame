package net.jueb.fuckGame.core.common.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class RoleChange implements Dto{
	
	private long money;
	private Map<Integer,Integer> items=new HashMap<>();
	public long getMoney() {
		return money;
	}
	public void setMoney(long money) {
		this.money = money;
	}
	public Map<Integer, Integer> getItems() {
		return items;
	}
	public void setItems(Map<Integer, Integer> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "RoleChange [money=" + money + ", items=" + items + "]";
	}
	
	public void clear()
	{
		this.money=0;
		this.items.clear();
	}
	
	@Override
	public void readFrom(ByteBuffer buffer) {
		money=buffer.readLong();
		Map<Integer,Integer> item=new HashMap<>();
		int size=buffer.readInt();
		for(int i=0;i<size;i++)
		{
			item.put(buffer.readInt(),buffer.readInt());
		}
		items=item;
	}
	@Override
	public void writeTo(ByteBuffer buffer) {
		int size=items.size();
		buffer.writeInt(size);
		for(Entry<Integer, Integer> e:items.entrySet())
		{
			buffer.writeInt(e.getKey());
			buffer.writeInt(e.getValue());
		}
	}
	
}
