package net.jueb.fuckGame.script.center.inner.gate;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import net.jueb.fuckGame.center.ServerMain;
import net.jueb.fuckGame.center.ServerQueue;
import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.base.RoomInfo;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.common.dto.RoleLoginRsp;
import net.jueb.fuckGame.core.common.dto.ServerResponse;
import net.jueb.fuckGame.core.common.dto.UserLoginInfo;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.script.center.inner.AbstractInnerScript;
import net.jueb.util4j.cache.callBack.CallBack;
import net.jueb.util4j.cache.callBack.impl.AnnotationCallBackImpl;
import net.jueb.util4j.cache.callBack.impl.CallBackFunction;
import net.jueb.util4j.common.game.IService.ServiceState;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 网关角色登陆
 * @author Administrator
 */
public class UserLoginScript extends AbstractInnerScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_UserLogin;
	}
	
	@CallBackFunction(id=1)
	protected void createRoleResult(ServerResponse<Role> result)
	{
		_log.debug("create Role result:"+result.getCode()+",role="+result.getResult());
		if(result.getCode()==GameErrCode.Succeed.value())
		{
			role=result.getResult();
			isCreate=true;
		}
		response(GameErrCode.valueOf(result.getCode()));
	}
	
	@CallBackFunction(id=2)
	protected void createRoleTimout()
	{
		response(GameErrCode.TimeOut);
		_log.error("创建角色超时,user="+user);
	}
	
	Role role;
	String callKey;
	boolean isCreate;//是否新建角色
	int lockedServerId;
	int gameRoomId=-1;
	NetConnection connection;
	UserLoginInfo user=new UserLoginInfo();
	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		if(ServerMain.getInstance().getState()!=ServiceState.Active)
		{
			response(GameErrCode.SystemReapir);
			return;
		}
		this.connection=connection;
		callKey=clientBuffer.readUTF();
		user.readFrom(clientBuffer);
		_log.debug("用户登录:"+user);
		if(StringUtils.isEmpty(user.getUid()))
		{
			response(GameErrCode.ArgsError);
			return ;
		}
		role=RoleCache.getInstance().getRoleByUid(user.getUid());
		if(role==null)
		{//创建角色
			CallBack<ServerResponse<Role>> call=new AnnotationCallBackImpl<ServerResponse<Role>>(this, 1, 2);
			IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_CreateRole, user,call);
			ServerMain.getInstance().getQueues().execute(ServerQueue.REG,script);
		}else
		{//角色登录
			lockedServerId=RoleGameLockManager.getInstance().lockedServer(role.getId());//角色锁定信息
			if(lockedServerId>0)
			{//角色锁定
				_log.debug("角色"+role.getId()+"已被锁定在服务器"+lockedServerId);
				GameController g=GameManager.getInstance().getById(lockedServerId);
				if(g!=null)
				{
					for(RoomInfo r:g.getRooms().values())
					{
						if(r.getRoles().contains(role.getId()))
						{
							gameRoomId=r.getRoomId();
							break;
						}
					}
				}else
				{
					_log.error("玩家锁定在服务器:"+lockedServerId+",但是服务器不存在");
				}
			}
			role.setLastLogin(new Date());
			role.getConfig().setLastLoginIp(user.getIp());
			role.setSex(user.getSex());
			role.setName(user.getName());
			role.setFaceIcon(user.getFaceIcon());
			role.setPhone(user.getMobile());
			response(GameErrCode.Succeed);
			_log.debug("角色登录:"+role);
		}
	}
	
	/**
	 * 回复
	 * @param code
	 */
	protected void response(GameErrCode code)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeUTF(callKey);
		buffer.writeInt(code.value());
		if(code==GameErrCode.Succeed)
		{//分配网关登陆token
			RoleLoginRsp rsp=new RoleLoginRsp();
			rsp.setName(role.getName());
			rsp.setFaceIcon(role.getFaceIcon());
			rsp.setRoleId(role.getId());
			rsp.setMoney(role.getMoney());
			rsp.setGameServerId(lockedServerId);
			rsp.getBagItem().putAll(role.getConfig().getBagItem());
			rsp.setSex(role.getSex());
			rsp.writeTo(buffer);
		}
		connection.sendMessage(new GameMessage(getMessageCode(),buffer));
	}
}
