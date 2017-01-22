package net.jueb.fuckGame.core.common.dto;

public class BaseServerResponse{

	protected int code;
	
	public BaseServerResponse(int code) {
		super();
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
