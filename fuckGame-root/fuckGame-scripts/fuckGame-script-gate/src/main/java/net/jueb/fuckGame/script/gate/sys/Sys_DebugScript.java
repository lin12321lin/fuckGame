package net.jueb.fuckGame.script.gate.sys;

import java.io.File;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.util.Log4jUtil;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;

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
		StringBuffer sb=new StringBuffer();
		Collection<RoleAgent> roles=RoleAgentManager.getInstance().values();
		sb.append("总玩家/链接数量:"+roles.size()+"/"+ServerMain.getInstance().getOnlineConnCount().get()
				+",总断线/重连次数:"+RoleAgent.totalDisConnect.get()+"/"+RoleAgent.totalReConnect.get()+"\n");
		for(RoleAgent ra:roles)
		{
			sb.append(ra.toString()+"\n");
		}
		_log.debug(sb.toString());
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
}
