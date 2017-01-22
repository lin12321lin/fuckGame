package net.jueb.fuckGame.script.center.sys;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.fuckGame.center.ServerConfig;
import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.util.Log4jUtil;

/**
 * 调试脚本
 * @author Administrator
 */
public class Sys_DebugScript extends AbstractSysScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Sys_Debug;
	}
	
	@Override
	public void action() {
		_log.debug("调试脚本运行");
	}
	
	/**
	 * 根据配置文件路径初始化日志配置
	 * @param logConfigpath
	 */
	public void reInitLogConfig(String logConfigpath)
	{
		try {
			 LoggerContext context =(LoggerContext)LogManager.getContext(false);
		     context.setConfigLocation(new File(logConfigpath).toURI());
		     //重新初始化Log4j2的配置上下文
		     context.reconfigure();
		     Logger log=LoggerFactory.getLogger(Log4jUtil.class);
			 log.info("日志配置重新初始化完成:"+logConfigpath);
			 System.err.println("日志更新完成");
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	void loadConfig()
	{
		if(!ServerMain.getInstance().hasAttribute("loadConfig2"))
		{
			ServerMain.getInstance().setAttribute("loadConfig2",1);
			try {
				ServerConfig.loadConfig();
				_log.debug("配置更新完成");
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
	}
}
