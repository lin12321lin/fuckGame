package net.jueb.fuckGame.gate;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import net.jueb.fuckGame.core.util.JProperties;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.util4j.common.game.env.Environment;
import net.jueb.util4j.hotSwap.classFactory.DefaultScriptSource;

public class ServerConfig {
	public static final Environment env=Environment.getInstance();
	public static final String SCRIPT_DIR = "C:/Users/Administrator/git/majong/majong-root/majong-scripts/majong-script-gate/target";
	public static String CONFIGURATION_FILE = "config.properties";
	public static JProperties SERVER_SETTINGS;
	public static boolean DEBUG;
	public static int SERVER_ID=4000;
	
	public static String BIND_HOST;
	public static int BIND_PORT;
	
	public static String LAN_HOST;
	public static int LAN_PORT;
	
	public static int CENTER_PORT;
	public static String CENTER_HOST;
	
	public static String TOKEN_API;
	
	public static String SCRIPT_SOURCE_DIR;//脚本资源目录
	
	public static DefaultScriptSource defaultScriptSource;
	
	static Logger log;
	
	static{
		Log4jUtil.initLogConfig(env.getLogConfigFile());
		log=Log4jUtil.getLogger(ServerConfig.class);
	}

	public static void loadConfig() throws Exception {
		SERVER_SETTINGS = new JProperties(env.getConfDir() + "/" + CONFIGURATION_FILE);
		DEBUG = Boolean.parseBoolean(SERVER_SETTINGS.getProperty("DEBUG"));
		SERVER_ID = Integer.parseInt(SERVER_SETTINGS.getProperty("SERVER_ID"));
		
		BIND_HOST = SERVER_SETTINGS.getProperty("BIND_HOST");
		BIND_PORT = Integer.parseInt(SERVER_SETTINGS.getProperty("BIND_PORT"));
		
		LAN_HOST = SERVER_SETTINGS.getProperty("LAN_HOST");
		LAN_PORT = Integer.parseInt(SERVER_SETTINGS.getProperty("LAN_PORT"));
		
		CENTER_HOST = SERVER_SETTINGS.getProperty("CENTER_HOST");
		CENTER_PORT = Integer.parseInt(SERVER_SETTINGS.getProperty("CENTER_PORT"));
		TOKEN_API =SERVER_SETTINGS.getProperty("TOKEN_API");
	
		SCRIPT_SOURCE_DIR=SERVER_SETTINGS.getProperty("SCRIPT_SOURCE_DIR");
		defaultScriptSource=new DefaultScriptSource(SCRIPT_SOURCE_DIR, TimeUnit.SECONDS.toMillis(10));
	}
}
