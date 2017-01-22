package net.jueb.fuckGame.core.script;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerScript implements IServerScript{

	protected final Logger _log=LoggerFactory.getLogger(getClass());
	private final List<Object> params=new ArrayList<Object>();
	private Request request;
	private RunMode mode;
	
	@Override
	public final void setRunMode(RunMode mode) {
		this.mode=mode;
	}

	@Override
	public final RunMode getRunMode() {
		return mode;
	}

	@Override
	public void setParams(Object... params) {
		for(Object param:params)
		{
			this.params.add(param);
		}
	}
	
	@Override
	public List<Object> getParams() {
		return params;
	}
	
	@Override
	public final Request getRequest() {
		return request;
	}
	
	@Override
	public final void setRequest(Request request) {
		this.request=request;
	}
	
	@Override
	public final void run() {
		try {
			switch (getRunMode()) {
			case Action:
				action();
				break;
			case HandleRequest:
				handleRequest(getRequest());
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			_log.error(e.getMessage(),e);
			throw e;
		}
	}
}
