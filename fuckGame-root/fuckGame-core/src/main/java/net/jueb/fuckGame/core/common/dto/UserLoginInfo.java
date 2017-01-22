package net.jueb.fuckGame.core.common.dto;

import net.jueb.fuckGame.core.common.enums.RoleSexEnum;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class UserLoginInfo implements Dto{

	private String uid;
	private String name;
	private String token;
	private RoleSexEnum sex;
	private String faceIcon;
	private String mobile;
	private String ip;
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
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public RoleSexEnum getSex() {
		return sex;
	}
	public void setSex(RoleSexEnum sex) {
		this.sex = sex;
	}
	public String getFaceIcon() {
		return faceIcon;
	}
	public void setFaceIcon(String faceIcon) {
		this.faceIcon = faceIcon;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@Override
	public void readFrom(ByteBuffer buffer) {
		uid=buffer.readUTF();
		name=buffer.readUTF();
		token=buffer.readUTF();
		sex=RoleSexEnum.valueOf(buffer.readByte());
		faceIcon=buffer.readUTF();
		mobile=buffer.readUTF();
		ip=buffer.readUTF();
	}
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeUTF(uid);
		buffer.writeUTF(name);
		buffer.writeUTF(token);
		buffer.writeByte(sex.getValue());
		buffer.writeUTF(faceIcon);
		buffer.writeUTF(mobile);
		buffer.writeUTF(ip);
	}
	
	@Override
	public String toString() {
		return "UserLoginInfo [uid=" + uid + ", name=" + name + ", token=" + token + ", sex=" + sex + ", faceIcon="
				+ faceIcon + ", mobile=" + mobile + ", ip=" + ip + "]";
	}
}
