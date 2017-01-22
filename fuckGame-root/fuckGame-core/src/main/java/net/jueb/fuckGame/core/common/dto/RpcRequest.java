package net.jueb.fuckGame.core.common.dto;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;

public class RpcRequest {

	private String callKey;
	private int code;
	private ByteBuffer content;
	private NetConnection from;
	public String getCallKey() {
		return callKey;
	}
	public void setCallKey(String callKey) {
		this.callKey = callKey;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public ByteBuffer getContent() {
		return content;
	}
	public void setContent(ByteBuffer content) {
		this.content = content;
	}
	public NetConnection getFrom() {
		return from;
	}
	public void setFrom(NetConnection from) {
		this.from = from;
	}
}
