package net.jueb.fuckGame.script.game.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.jueb.fuckGame.core.net.message.GameMsgCode;

/**
 * 凌晨任务
 * @author Administrator
 */
public class Sys_Time24Script extends AbstractSysScript{

	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	
	@Override
	public int getMessageCode() {
		return GameMsgCode.Sys_Time24;
	}

	@Override
	public void action() {
		_log.info("凌晨任务开始:"+sdf.format(new Date()));
	}
	
}
