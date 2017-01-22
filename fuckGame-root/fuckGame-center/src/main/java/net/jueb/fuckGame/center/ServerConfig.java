package net.jueb.fuckGame.center;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;

import net.jueb.fuckGame.core.common.dto.def.Def_Item;
import net.jueb.fuckGame.core.util.JProperties;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.util4j.common.game.env.Environment;
import net.jueb.util4j.hotSwap.classFactory.DefaultScriptSource;
import net.jueb.util4j.hotSwap.classFactory.DefaultScriptSource.ScanFilter;

public class ServerConfig {
	private static Logger log;
	public static final Environment env=Environment.getInstance();
	public static String CONFIGURATION_FILE = "config.properties";
	public static String SCRIPT_SOURCE_DIR;//脚本资源目录
	public static JProperties SERVER_SETTINGS;
	
	public static int SERVER_ID=2000;
	
	public static String BIND_HOST="0.0.0.0";
	public static int BIND_PORT=4000;
	public static int BIND_HTTP_PORT=4001;
	
	public static final Map<Integer,Def_Item> DEF_ITEM=new HashMap<Integer,Def_Item>();
	public static DefaultScriptSource defaultScriptSource;
	
	static{
		Log4jUtil.initLogConfig(env.getLogConfigFile());
		log=Log4jUtil.getLogger(ServerConfig.class);
	}

	
	public static void loadConfig() throws Exception {
		SERVER_SETTINGS = new JProperties(env.getConfDir() + "/" + CONFIGURATION_FILE);
		SERVER_ID = Integer.parseInt(SERVER_SETTINGS.getProperty("SERVER_ID"));
		BIND_HOST=SERVER_SETTINGS.getProperty("BIND_HOST");
		BIND_PORT = Integer.parseInt(SERVER_SETTINGS.getProperty("BIND_PORT"));
		BIND_HTTP_PORT = Integer.parseInt(SERVER_SETTINGS.getProperty("BIND_HTTP_PORT"));
		DEF_ITEM.clear();
		for(Def_Item def:loadDef_Item())
		{
			DEF_ITEM.put(def.getId(), def);
		}
		SCRIPT_SOURCE_DIR=SERVER_SETTINGS.getProperty("SCRIPT_SOURCE_DIR");
		ScanFilter scanFilter=new ScanFilter(){
			@Override
			public boolean accpetJars() {
				return false;
			}
			@Override
			public boolean accpetDirClass() {
				return true;
			}
		};
		defaultScriptSource=new DefaultScriptSource(SCRIPT_SOURCE_DIR, TimeUnit.SECONDS.toMillis(10),scanFilter);
	}

	public static List<Def_Item> loadDef_Item()
	{
		List<Def_Item> list=new ArrayList<Def_Item>();
		String path = env.getConfDir() + "/item_config.xml";
		File f = new File(path);
		if(f.exists() && f.isFile())
		{
			try {
				FileInputStream inputStream = new FileInputStream(f);
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(inputStream);
				Element rootElement = document.getRootElement();
				@SuppressWarnings("unchecked")
				List<Element> elementList = rootElement.elements("item");
				for (Element element : elementList) 
				{
					Def_Item df = new Def_Item();
					df.setId(Integer.parseInt(element.attributeValue("id")));
					df.setName(element.attributeValue("name",""));
					list.add(df);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(),e);
			}
		}
		return list;
	}
}
