package net.jueb.fuckGame.gate.factory;

import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.core.script.ServerScriptProvider;
import net.jueb.fuckGame.gate.ServerConfig;

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
