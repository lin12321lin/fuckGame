package net.jueb.fuckGame.center.factory;

import net.jueb.fuckGame.center.ServerConfig;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.core.script.ServerScriptProvider;

public class ScriptFactory extends ServerScriptProvider<IServerScript>{

	public ScriptFactory() {
		super(ServerConfig.defaultScriptSource, true);
	}

	private static ScriptFactory factory;
	
	public static ScriptFactory getInstance()
	{
		if(factory==null)
		{
			factory=new ScriptFactory();
		}
		return factory;
	}

	@Override
	protected void initStaticScriptRegist(StaticScriptRegister reg) {
		
	}
}
