package net.jueb.fuckGame.core.common;

public class JsonResult {

	public int code;
	
	public Object result="";

	public JsonResult(int code) {
		this(code, null);
	}
	
	public JsonResult(int code, Object result) {
		super();
		this.code = code;
		setResult(result);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		if(result==null)
		{
			this.result="";
		}else
		{
			this.result = result;
		}
	}

	@Override
	public String toString() {
		return "JsonResult [code=" + code + ", result=" + result + "]";
	}
}
