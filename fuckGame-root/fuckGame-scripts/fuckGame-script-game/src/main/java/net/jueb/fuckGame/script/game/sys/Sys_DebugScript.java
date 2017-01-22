package net.jueb.fuckGame.script.game.sys;

import net.jueb.fuckGame.core.net.message.GameMsgCode;

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
		
	}
}
