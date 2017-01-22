package net.jueb.fuckGame.script.center.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.jueb.fuckGame.center.base.GameController;
import net.jueb.fuckGame.center.base.RoleCache;
import net.jueb.fuckGame.center.base.RoomInfo;
import net.jueb.fuckGame.center.factory.ScriptFactory;
import net.jueb.fuckGame.center.manager.GameManager;
import net.jueb.fuckGame.center.manager.RoleGameLockManager;
import net.jueb.fuckGame.center.manager.RoleGateLockManager;
import net.jueb.fuckGame.core.common.dto.GameEntryInfo;
import net.jueb.fuckGame.core.common.dto.Role;
import net.jueb.fuckGame.core.common.dto.RoomNumber;
import net.jueb.fuckGame.core.common.dto.RoleLock.LockServerType;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.net.message.RoleGameMessage;
import net.jueb.util4j.cache.callBack.impl.AnnotationCallBackImpl;
import net.jueb.util4j.cache.callBack.impl.CallBackFunction;
import net.jueb.util4j.hotSwap.classFactory.IScript;

/**
 * 玩家请求进入游戏服务器,或者进入房间号所在的服务器
 * 如果玩家之前已经存在某房间,使用邀请码进入,则阻止
 */
public class EntryGameScript extends AbstractRoleGameActionScript{

	@Override
	public int getMessageCode() {
		return GameMsgCode.Center_EntryGame;
	}
	
	@Override
	public void action() {
		
	}

	NetConnection connection;
	GameController game;
	NetConnection gameConn;
	RoleGameMessage action;
	Role role;
	@Override
	protected void handleAction(NetConnection connection, RoleGameMessage action) {
		this.connection=connection;
		this.action=action;
		long roleId=action.getRoleId();
		role=RoleCache.getInstance().getRoleById(roleId);
		if(role==null)
		{
			response(GameErrCode.RoleNotFound);
			return;
		}
		ByteBuffer buffer=action.getMsg().getContent();
		int roomNumberValue=buffer.readInt();//是否是带房间号,如果大于0则表示是加入房间或者进入原有房间
		RoomNumber roomNumber=RoomNumber.valueOf(roomNumberValue);
		_log.debug("请求进入游戏服务器:roleId="+role.getId()+",roomNumberValue="+roomNumberValue+",roomNumber="+roomNumber);
		List<GameController> gamelist=new ArrayList<>(GameManager.getInstance().getList());
		if(gamelist.isEmpty())
		{//如果一个游戏服都没有则提示服务不可用
			response(GameErrCode.GameServiceNotAvailable);
			return;
		}
		int lockedServerId=RoleGameLockManager.getInstance().lockedServer(role.getId());//当前玩家所在服务器
		int gateId=RoleGateLockManager.getInstance().lockedServer(role.getId());//当前玩家所在网关
		//得到服务器
		if(roomNumberValue>0)
		{//邀请码进入房间服务器
			int sid=roomNumber.getServerId();
			GameController g=GameManager.getInstance().getById(sid);
			if(g==null)
			{//找不到服务器
				response(GameErrCode.RoomNumberError);
				return;
			}
			RoomInfo roomInfo=g.getRooms().get(roomNumber.getRoomId());
			if(roomInfo==null)
			{//找不到房间
				response(GameErrCode.RoomNumberError);
				return;
			}
			if(lockedServerId>0 && g.getServerInfo().getServerId()!=lockedServerId)
			{//已存在某服务器不允许进入其它服务器
				response(GameErrCode.UnSupportOperation);
				return;
			}
			game=g;
		}else
		{//随机进入服务器
			Collections.sort(gamelist);
			game=gamelist.get(0);
		}
		gameConn=game.getConnection();//此次请求使用的链路
		GameEntryInfo entry=new GameEntryInfo();
		entry.setBag(role.getConfig().getBagItem());
		entry.setFaceIcon(role.getFaceIcon());
		entry.setGateId(gateId);
		entry.setIp(role.getConfig().getLastLoginIp());
		entry.setMoney(role.getMoney());
		entry.setRoomNumber(roomNumberValue);//如果服务器有房间号则忽略此房间号
		entry.setName(role.getName());
		entry.setRoleId(role.getId());
		AnnotationCallBackImpl<ByteBuffer> call=new AnnotationCallBackImpl<>(this, 1, 2);
		call.setTimeOut(TimeUnit.SECONDS.toMillis(10));
		IScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Game_RoleEntry,gameConn,entry,call);
		script.run();
	}
	
	@CallBackFunction(id=1)
	protected void call(ByteBuffer rsp)
	{
		int code=rsp.readInt();
		if(code==GameErrCode.Succeed.value())
		{
			int gameId=game.getServerInfo().getServerId();
			RoleGameLockManager.getInstance().lock(role.getId(),gameId);
			broadcastRoleLockUpdate(gameId, LockServerType.Game, true, role.getId());
		}
		response(GameErrCode.valueOf(code));
	}
	
	@CallBackFunction(id=2)
	protected void timeOutCall()
	{
		response(GameErrCode.TimeOut);
		_log.error("进入游戏服务器超时:roleId="+role.getId()+",game="+game);
	}
	
	private void response(GameErrCode code)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(code.value());
		responseAction(new GameMessage(getMessageCode(), buffer));
	}
}