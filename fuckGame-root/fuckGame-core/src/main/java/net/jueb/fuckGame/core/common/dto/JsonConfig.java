package net.jueb.fuckGame.core.common.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jueb.fuckGame.core.common.ItemAmount;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class JsonConfig implements Dto{
	
	/**
	 * 上次登录IP
	 */
	private String lastLoginIp="";
	
	/**
	 * 物品
	 */
	private Map<Integer,Integer> bagItem=new HashMap<Integer,Integer>();
	
	private transient Object bagLock=new Object();
	
	/**
	 * 只能读,不能改
	 * @return
	 */
	public Map<Integer, Integer> getBagItem() {
		return Collections.unmodifiableMap(bagItem);
	}

	public void setBagItem(Map<Integer, Integer> bagItem) {
		synchronized (bagLock) {
			this.bagItem.clear();
			if(bagItem!=null)
			{
				this.bagItem.putAll(bagItem);
			}
		}
	}

	/**
	 * 增量添加物品
	 * 如果物品最终数量<=0则会移除物品
	 * @param ia
	 */
	public void _bagItemAdd(ItemAmount ia)
	{
		if(ia!=null)
		{
			_bagItemAdd(ia.getItemId(), ia.getCount());
		}
	}

	/**
	 * 增量添加物品
	 * 如果物品最终数量<=0则会移除物品
	 * @param itemId
	 * @param count
	 */
	public void _bagItemAdd(int itemId,int count)
	{
		synchronized (bagLock) {
			int newCount=getBagItem().getOrDefault(itemId,0)+count;
			if(newCount<=0)
			{
				bagItem.remove(itemId);
			}else
			{
				bagItem.put(itemId, newCount);
			}
		}
	}

	/**
	 * 更新背包物品,如果物品数量<=0则会移除物品
	 * @param itemId
	 * @param count
	 */
	public void _bagItemUpdate(int itemId,int count)
	{
		synchronized (bagLock) {
			if(count<=0)
			{
				bagItem.remove(itemId);
			}else
			{
				bagItem.put(itemId,count);
			}
		}
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	@Override
	public String toString() {
		return "JsonConfig [lastLoginIp=" + lastLoginIp + ", bagItem=" + bagItem + "]";
	}

	@Override
	public void readFrom(ByteBuffer buffer) {
		lastLoginIp=buffer.readUTF();
		Map<Integer,Integer> item=new HashMap<>();
		int size=buffer.readInt();
		for(int i=0;i<size;i++)
		{
			item.put(buffer.readInt(),buffer.readInt());
		}
		bagItem=item;
	}

	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeUTF(lastLoginIp);
		int size=bagItem.size();
		buffer.writeInt(size);
		for(Entry<Integer, Integer> e:bagItem.entrySet())
		{
			buffer.writeInt(e.getKey());
			buffer.writeInt(e.getValue());
		}
	}
}
