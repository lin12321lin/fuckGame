package net.jueb.fuckGame.game.base;

import java.util.HashMap;
import java.util.Map;

import net.jueb.fuckGame.core.common.dto.RoleChange;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.game.factory.ScriptFactory;
import net.jueb.fuckGame.game.manager.GateManager;

public class RoleController{
	
	private final long id;
	private String name;
	private long money;
	private Map<Integer,Integer> bag=new HashMap<>();
	private String faceIcon;
	private String ip;
	private int gateServer;
	private RoomController room;
	private boolean online;
	
	private long lastOffLineTime;

	public RoleController(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
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
	public Map<Integer, Integer> getBag() {
		return bag;
	}

	public void setBag(Map<Integer, Integer> bag) {
		this.bag = bag;
	}

	public String getFaceIcon() {
		return faceIcon;
	}

	public void setFaceIcon(String faceIcon) {
		this.faceIcon = faceIcon;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getGateServer() {
		return gateServer;
	}

	public void setGateServer(int gateServer) {
		this.gateServer = gateServer;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
		if(!online)
		{
			lastOffLineTime=System.currentTimeMillis();
		}
	}

	public long getLastOffLineTime() {
		return lastOffLineTime;
	}

	public void setLastOffLineTime(long lastOffLineTime) {
		this.lastOffLineTime = lastOffLineTime;
	}

	public RoomController getRoom() {
		return room;
	}
	/**
	 * 此接口只能被相邻的房间控制器调用
	 * @param room
	 */
	void setRoom(RoomController room) {
		this.room = room;
	}

	public void sendMsg(GameMessage msg)
	{
		GateController gc=GateManager.getInstance().get(gateServer);
		if(gc!=null)
		{
			long roleId=this.getId();
			RoleGameMessage rmsg=new RoleGameMessage(roleId, msg);
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Gate_RoleGameMessage,gc.getConnection(),rmsg);
			if(script!=null)
			{
				script.run();
			}
		}
	}

	@Override
	public String toString() {
		return "RoleController [id=" + id + ", name=" + name + ", money=" + money + ", bag=" + bag + ", faceIcon="
				+ faceIcon + ", ip=" + ip + ", gateServer=" + gateServer + ", room=" + room==null?null:room.getNumber() + ", online=" + online
				+ ", change=" + change + "]";
	}

	protected boolean can_updateMoney(long value)
	{
		return getMoney()+value>=0;
	}
	
	protected void _updateMoney(long value)
	{
		setMoney(getMoney()+value);
	}	
	
	protected boolean can_updateItem(int itemId,int count)
	{
		return bag.getOrDefault(itemId, 0)+count>0;
	}
	
	protected void _updateItem(int itemId,int count)
	{
		bag.put(itemId, bag.getOrDefault(itemId, 0)+count);
	}	
	
	private transient Object updateLock=new Object();
	
	/**
	 * 增量更新金币
	 * @param value
	 */
	public boolean updateMoney(long value)
	{
		synchronized (updateLock) {
			if(can_updateMoney(value))
			{
				setMoney(getMoney()+value);
				change.setMoney(change.getMoney()+value);
				return true;
			}else
			{
				return false;
			}
		}
	}
	
	/**
	 * 增量更新物品
	 * @param itemId
	 * @param count
	 */
	public boolean updateItem(int itemId,int count)
	{
		synchronized (updateLock) {
			if(can_updateItem(itemId,count))
			{
				_updateItem(itemId, count);
				change.getItems().put(itemId,change.getItems().getOrDefault(itemId, 0)+count);
				return true;
			}else
			{
				return false;
			}
		}
	}
	
	private final RoleChange change=new RoleChange();
	
	/**
	 * 推送更新到大厅
	 */
	public void pushChangeToCenter()
	{
		synchronized (updateLock) {
			RoleChange pushchange=new RoleChange();
			pushchange.setMoney(change.getMoney());
			pushchange.getItems().putAll(change.getItems());
			change.clear();
			IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Game_RoleDataUpdate, pushchange);
			script.run();
		}
	}
}
