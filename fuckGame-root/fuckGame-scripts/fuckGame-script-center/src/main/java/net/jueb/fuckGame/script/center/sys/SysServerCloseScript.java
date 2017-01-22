package net.jueb.fuckGame.script.center.sys;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.gson.Gson;

import net.jueb.fuckGame.center.ServerConfig;
import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.core.common.dto.Role;
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
		saveRoles();
		_log.warn("关服脚本执行完毕");
	}

	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_hh.mm.ss");
	
	
	
	
	protected void saveRoles()
	{
		_log.warn("保存所有未更新角色…………");
		RoleCache roleCache=RoleCache.getInstance();
		Set<Long> ids=roleCache.pollUpdates();
		if(ids.isEmpty())
		{
			return ;
		}
		Queue<Role> roles=new LinkedBlockingQueue<Role>();
		for(long roleId:ids)
		{
			Role role=roleCache.getRoleById(roleId);
			roles.add(role);
		}
		List<Role> saveFailRoles=saveRoles(roles);
		saveOfflineRoles(saveFailRoles);//保存剩余的失败更新玩家
	}
	
	/**
	 * 保存缓存到文件
	 * @param roles
	 */
	protected void saveOfflineRoles(Collection<Role> roles)
	{
		if(roles.isEmpty())
		{
			return ;
		}
		Gson gson=new Gson();
		String json=gson.toJson(roles);
		String fileName = ServerConfig.env.getLoaclDataDir() + "/"+sdf.format(new Date()) + ".roleCache";
		try {
			FileOutputStream fos=new FileOutputStream(fileName);
			fos.write(json.getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			_log.error(e.getMessage(),e);
		}
	}
	
	@Override
	public void handleRequest(Request request) {
		
	}
}
