package net.jueb.fuckGame.core.common.dto;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

/**
 * 服务端口和地址
 * @author Administrator
 */
public class Address implements Dto{

	/**
	 * 服务地址
	 */
	public String host;
	/**
	 * 服务端口
	 */
	public int port;
	
	public Address(String host,int port) {
		this.host = host;
		this.port = port;
	}
	public Address() {
	}
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	@Override
	public void readFrom(ByteBuffer buffer) {
		this.host=buffer.readUTF();
		this.port=buffer.readInt();
	}
	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeUTF(host);
		buffer.writeInt(port);
	}
	@Override
	public String toString() {
		return "Address [host=" + host + ", port=" + port + "]";
	}
}
