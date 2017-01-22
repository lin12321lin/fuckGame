package net.jueb.fuckGame.core.common.dto;

public class ServerResponse<T> extends BaseServerResponse{

	private T result;
	
	public ServerResponse(int code, T result) {
		super(code);
		this.result = result;
	}
	
	public T getResult() {
		return result;
	}
	
	public void setResult(T result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ServerResponse [code=" + code + ", result=" + result + "]";
	}
}
