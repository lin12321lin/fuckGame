package net.jueb.fuckGame.core.common.dto;

import java.io.Serializable;
import java.util.Date;

import net.jueb.fuckGame.core.common.enums.RoleSexEnum;
import net.jueb.fuckGame.core.common.enums.RoleStatus;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;
public class Role implements Serializable,Dto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3252807187756373227L;
	
	/**
	 * 角色ID
	 */
	private long id;
	/**
	 * 用户ID
	 */
	private String uid;
	/**
	 * 角色名
	 */
	private String name;
	/**
	 * 角色
	 */
	private long money;
	/**
	 * 角色性别
	 */
	private RoleSexEnum sex;
	/**
	 * 手机
	 */
	private String phone;
	
	/**
	 * 头像
	 */
	private String faceIcon="";
	
	/**
	 * 角色配置信息
	 */
	private JsonConfig config;

	/**
	 * 状态
	 */
	private RoleStatus status;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 最后一次登陆时间
	 */
	private Date lastLogin;
	
	public Role() {
			createTime=new Date();
			lastLogin=new Date(createTime.getTime());
			status=RoleStatus.Enable;
			sex=RoleSexEnum.undefine;
			config=new JsonConfig();
	}
	
	public JsonConfig getConfig() {
		return config;
	}

	public void setConfig(JsonConfig config) {
		this.config = config;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public RoleSexEnum getSex() {
		return sex;
	}

	public void setSex(RoleSexEnum sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public RoleStatus getStatus() {
		return status;
	}

	public void setStatus(RoleStatus status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getFaceIcon() {
		return faceIcon;
	}
	
	public String getFaceIconDefaultEmpty() {
		return faceIcon==null?"":faceIcon;
	}

	public void setFaceIcon(String faceIcon) {
		this.faceIcon = faceIcon;
	}

	@Override
	public void readFrom(ByteBuffer buffer) {
		this.id=buffer.readLong();
		this.uid=buffer.readUTF();
		this.name=buffer.readUTF();
		this.money=buffer.readLong();
		this.sex=RoleSexEnum.valueOf(buffer.readByte());
		this.phone=buffer.readUTF();
		JsonConfig config=new JsonConfig();
		config.readFrom(buffer);
		this.config=config;
		this.status=RoleStatus.valueOf(buffer.readByte());
		this.createTime=new Date(buffer.readLong());
		this.lastLogin=new Date(buffer.readLong());
		this.faceIcon=buffer.readUTF();
	}
	
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeLong(id);
		buffer.writeUTF(uid);
		buffer.writeUTF(name);
		buffer.writeLong(money);
		buffer.writeByte(sex.getValue());
		buffer.writeUTF(phone);
		config.writeTo(buffer);
		buffer.writeByte(status.value());
		buffer.writeLong(createTime.getTime());
		buffer.writeLong(lastLogin.getTime());
		buffer.writeUTF(faceIcon);
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", uid=" + uid + ", name=" + name + ", money=" + money + ", sex=" + sex + ", phone="
				+ phone + ", faceIcon=" + faceIcon + ", config=" + config + ", status=" + status + ", createTime="
				+ createTime + ", lastLogin=" + lastLogin + "]";
	}
}
