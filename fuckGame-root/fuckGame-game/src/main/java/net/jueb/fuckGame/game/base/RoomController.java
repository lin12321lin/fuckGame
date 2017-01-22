package net.jueb.fuckGame.game.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.jueb.fuckGame.core.common.dto.RoomNumber;
import net.jueb.fuckGame.core.net.message.GameMessage;

public class RoomController{
	
	private final RoomNumber number;
	
	/**
	 * 房主
	 */
	private TableLocationEnum master;
	
	/**
	 * 庄家
	 */
	private TableLocationEnum banker;
	
	/**
	 * 位置
	 */
	private RoleController[] location=new RoleController[4];
	
	/**
	 * 状态
	 */
	private TableStateEnum state=TableStateEnum.Created;

	public RoomController(RoomNumber number) {
		super();
		this.number = number;
	}
	
	public RoomNumber getNumber() {
		return number;
	}

	public TableLocationEnum getMaster() {
		return master;
	}

	public void setMaster(TableLocationEnum master) {
		this.master = master;
	}

	public TableLocationEnum getBanker() {
		return banker;
	}

	public void setBanker(TableLocationEnum banker) {
		this.banker = banker;
	}
	
	/**
	 * 获取位置上的玩家
	 * @param location
	 * @return
	 */
	public RoleController getRole(TableLocationEnum location)
	{
		return this.location[location.getValue()];
	}
	
	/**
	 * 设置玩家位置,新玩家的房间会设置为当前房间
	 * @param role 
	 * @param location
	 * @return 返回原来位置的玩家,且原来玩家的房间设置为null
	 */
	public RoleController setRole(RoleController role,TableLocationEnum location)
	{
		RoleController old=this.location[location.getValue()];
		if(old!=null)
		{
			old.setRoom(null);
		}
		this.location[location.getValue()]=role;
		if(role!=null)
		{
			role.setRoom(this);
		}
		return old;
	}
	
	/**
	 * 获取所有玩家
	 * @return
	 */
	public List<RoleController> getRoles()
	{
		List<RoleController> list=new ArrayList<>();
		for(RoleController r:location)
		{
			if(r!=null)
			{
				list.add(r);
			}
		}
		return list;
	}
	
	/**
	 * 获取该玩家所在位置信息
	 * @param role
	 * @return
	 */
	public TableLocationEnum getLocation(RoleController role)
	{
		for(int i=0;i<location.length;i++)
		{
			if(location[i]==role)
			{
				return TableLocationEnum.valueOf(i);
			}
		}
		return null;
	}
	
	/**
	 * 是否存在玩家
	 * @param role
	 * @return
	 */
	public boolean hasRole(RoleController role)
	{
		return getLocation(role)!=null;
	}
	
	public TableStateEnum getState() {
		return state;
	}

	public void setState(TableStateEnum state) {
		this.state = state;
		this.state.setStateTime(System.currentTimeMillis());
	}

	public void broadcast(GameMessage msg)
	{
		for(RoleController role:location)
		{
			if(role!=null)
			{
				role.sendMsg(msg);
			}
		}
	}
	
	public void broadcast(GameMessage msg,long excludeRoleId)
	{
		for(RoleController role:location)
		{
			if(role!=null)
			{
				if(role.getId()!=excludeRoleId)
				{
					role.sendMsg(msg);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "RoomController [number=" + number + ", master=" + master + ", banker=" + banker + ", location="
				+ Arrays.toString(location) + ", state=" + state + "]";
	}
}
