package net.jueb.fuckGame.game.factory;

import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.core.script.ServerScriptProvider;
import net.jueb.fuckGame.game.ServerConfig;

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
