package net.jueb.fuckGame.core.net.message;

public enum GameErrCode {

	/**
	 * 未知错误
	 */
	UnknownError(0,"未知错误"),
	/**
	 * 成功
	 */
	Succeed (1,"成功"),
	/**
	 * 参数错误
	 */
	ArgsError (2,"参数错误"),
	/**
	 * 登录token错误功
	 */
	GateTokenError (3,"登录token错误"),
	/**
	 * 重复服务ID注册
	 */
	RepeatServerIdRegError (4,"重复服务ID注册"),
	/**
	 * 房间号错误
	 */
	RoomNumberError (5,"房间号错误"),
	/**
	 * 系统繁忙
	 */
	ServiceBusy (6,"系统繁忙"),
	/**
	 * 处理超时
	 */
	TimeOut (7,"处理超时"),
	/**
	 * 角色游戏锁定
	 */
	RoleGameLockError (8,"角色游戏锁定"),
	/**
	 * 角色不存在
	 */
	RoleNotFound (9,"处理超时"),
	/**
	 * 不支持的操作
	 */
	UnSupportOperation (10,"不支持的操作"),
	/**
	 * 服务部可用
	 */
	ServiceNotAvailable(11,"服务不可用"),
	/**
	 * 游戏服务不可用
	 */
	GameServiceNotAvailable(12,"游戏服务不可用"),
	/**
	 * 大厅服务不可用
	 */
	HallServiceNotAvailable(13,"大厅服务不可用"),
	/**
	 * 服务器维护中
	 */
	SystemReapir(14,"服务器维护中"),
	/**
	 * webtoken错误
	 */
	WebTokenError (15,"webtoken错误"),
	/**
	 * 已经存在某房间,不可创建房间
	 */
	InOtherRoomError (16,"已经存在某房间"),
	/**
	 * 服务器房间上限
	 */
	ServerRoomLimit (17,"服务器房间上限,不可创建房间"),
	/**
	 * 房间不存在
	 */
	RoomNotFound (18,"房间不存在"),
	/**
	 * 房间人数已满/房间已经开始游戏
	 */
	RoomRoleLimit (19,"房间人数已满/房间已经开始游戏"),
	/**
	 * 没有权限
	 */
	NoPermissions(20,"没有权限"),
	;
	
	private int value;
	private final String msg;
	private GameErrCode(int value,String msg) {
		this.value=value;
		this.msg=msg;
	}
	public int value()
	{
		return this.value;
	}
	
	public String getMsg() {
		return msg;
	}
	public static GameErrCode valueOf(int value)
	{
		for(GameErrCode gt:values())
		{
			if(gt.value==value)
			{
				return gt;
			}
		}
		return UnknownError;
	}
}
