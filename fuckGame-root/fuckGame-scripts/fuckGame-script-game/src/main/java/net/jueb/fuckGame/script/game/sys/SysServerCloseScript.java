package net.jueb.fuckGame.script.game.sys;

import net.jueb.fuckGame.core.net.message.GameMsgCode;

/**
 * 凌晨任务
 * @author Administrator
 */
public class SysServerCloseScript extends AbstractSysScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Sys_ServerClose;
	}

	@Override
	public void action() {
		_log.warn("关服脚本执行…………");
		_log.warn("关服脚本执行完毕");
	}
}
