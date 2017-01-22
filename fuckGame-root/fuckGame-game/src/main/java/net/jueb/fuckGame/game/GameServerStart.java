package net.jueb.fuckGame.game;

import java.io.IOException;

import net.jueb.util4j.common.game.IService.ServiceState;


public class GameServerStart {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			ServerConfig.CONFIGURATION_FILE = args[0];
			if (args.length > 1) {
				ServerConfig.env.initEnv(args[1]);
			}
		}
		ServerConfig.loadConfig();
		ServerMain main = ServerMain.getInstance();
		main.startService();
		if(main.getState()!=ServiceState.Active)
		{
			System.exit(0);
		}
	}
}
