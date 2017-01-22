package net.jueb.fuckGame.core.script;

import java.util.Set;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.script.IServerScript.Request;
import net.jueb.fuckGame.core.script.IServerScript.RunMode;
import net.jueb.util4j.hotSwap.classFactory.AbstractScriptProvider;
import net.jueb.util4j.hotSwap.classFactory.ScriptSource;

/**
 * T尽量使用接口类型
 * @author juebanlin@gmail.com
 * time:2015年6月17日
 * @param <T>
 */
public abstract class ServerScriptProvider<T extends IServerScript> extends AbstractScriptProvider<T>{

	public ServerScriptProvider(ScriptSource scriptSource, boolean autoReload) {
		super(scriptSource, autoReload);
	}

	public ServerScriptProvider(ScriptSource scriptSource) {
		super(scriptSource);
	}

	public Set<Integer> getRegisgCode()
	{
		return codeMap.keySet();
	}
	
	public final T buildHandleRequest(int code,NetConnection connection,Object msg,Object ...params)
	{
		T script=super.buildInstance(code);
		if(script!=null)
		{
			script.setRunMode(RunMode.HandleRequest);
			script.setRequest(new Request(connection, msg));
			if(params!=null)
			{
				script.setParams(params);
			}
		}
		return script;
	}
	
	public final T buildAction(int code,Object ...params)
	{
		T script=super.buildInstance(code);
		if(script!=null)
		{
			script.setRunMode(RunMode.Action);
			if(params!=null)
			{
				script.setParams(params);
			}
		}
		return script;
	}
}
