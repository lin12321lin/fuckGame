package net.jueb.fuckGame.core.common;

import net.jueb.fuckGame.core.common.dto.Address;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class ServerInfo implements Dto{
	
	private int serverId;
	
	private Address address;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void readFrom(ByteBuffer buffer) {
		setServerId(buffer.readInt());
		Address innerHost=new Address();
		innerHost.readFrom(buffer);
		setAddress(innerHost);
	}

	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeInt(getServerId());
		getAddress().writeTo(buffer);
	}

	@Override
	public String toString() {
		return "ServerInfo [serverId=" + serverId + ", address=" + address + "]";
	}
}
