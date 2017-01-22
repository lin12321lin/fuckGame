package net.jueb.fuckGame.script.center.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.jueb.fuckGame.center.base.RoleCache;
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
		syncAllRoles();
	}
	
	
	/**
	 * 标记所有角色需要更新
	 */
	protected void syncAllRoles()
	{
		_log.info("标记同步角色开始");
		RoleCache roleCache=RoleCache.getInstance();
		for(long roleId:roleCache.roleIds())
		{
			roleCache.updateMark(roleId);
		}
		_log.info("标记同步角色结束");
	}
}
