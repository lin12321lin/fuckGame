package net.jueb.fuckGame.core.common;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class ItemAmount implements Dto{

	private int itemId;
	private int count;
	public ItemAmount() {
	}
	public ItemAmount(int itemId, int count) {
		super();
		this.itemId = itemId;
		this.count = count;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "ItemAmount [itemId=" + itemId + ", count=" + count + "]";
	}
	@Override
	public void readFrom(ByteBuffer buffer) {
		itemId=buffer.readInt();
		count=buffer.readInt();
	}
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeInt(itemId);
		buffer.writeInt(count);
	}
}
