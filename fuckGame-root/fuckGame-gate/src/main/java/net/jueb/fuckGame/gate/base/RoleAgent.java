package net.jueb.fuckGame.gate.base;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.GameMessage;

/**
 * 角色代理
 * @author Administrator
 */
public class RoleAgent {
	
	private Logger log=LoggerFactory.getLogger(getClass());
	/**
	 * 断线重连等待时间
	 */
	public final static long RECONNECT_TIME=TimeUnit.SECONDS.toMillis(60);
	/**
	 * 累计断线次数
	 */
	public static final AtomicLong totalDisConnect=new AtomicLong();
	
	/**
	 * 累计重连次数
	 */
	public static final AtomicLong totalReConnect=new AtomicLong();
	
	/**
	 * 角色ID
	 */
	private long roleId;
	
	/**
	 * 角色名称
	 */
	private String roleName;
	
	private GateLoginRsp lastRspInfo;
	
	/**
	 * 所在游戏服务器ID
	 */
	private int serverId=-1;
	
	/**
	 * 网关登录时间
	 */
	private long loginTime;
	
	/**
	 * 上一次收到客户端消息的时间
	 */
	private long lastReadTime;
	
	/**
	 * 登录使用token
	 */
	private String loginToken;
	
	/**
	 * 断线重连token
	 */
	private String reToken;
	
	/**
	 * 上一次断线时间
	 */
	private long lastOffTime;
	
	/**
	 * 上一次登录地址
	 */
	private InetSocketAddress lastAddress;
	
	/**
	 * 客户端连接
	 */
	private NetConnection connection;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public GateLoginRsp getLastRspInfo() {
		return lastRspInfo;
	}

	public void setLastRspInfo(GateLoginRsp lastRspInfo) {
		this.lastRspInfo = lastRspInfo;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		log.debug("RoleAgent("+roleId+") Forward ServerId["+this.serverId+"]===>["+serverId+"]");
		this.serverId = serverId;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public String getReToken() {
		return reToken;
	}

	public void setReToken(String reToken) {
		this.reToken = reToken;
	}

	public long getLastOffTime() {
		return lastOffTime;
	}

	public void setLastOffTime(long lastOffTime) {
		this.lastOffTime = lastOffTime;
	}

	public long getLastReadTime() {
		return lastReadTime;
	}

	public void setLastReadTime(long lastReadTime) {
		this.lastReadTime = lastReadTime;
	}

	public InetSocketAddress getLastAddress() {
		return lastAddress;
	}

	public void setLastAddress(InetSocketAddress lastAddress) {
		this.lastAddress = lastAddress;
	}

	public NetConnection getConnection() {
		return connection;
	}

	public void setConnection(NetConnection connection) {
		this.connection = connection;
	}

	public void sendMessage(GameMessage msg)
	{
		if(connection!=null)
		{
			connection.sendMessage(msg);
		}
	}

	@Override
	public String toString() {
		return "RoleAgent [roleId=" + roleId + ", roleName=" + roleName + ", lastRspInfo=" + lastRspInfo + ", serverId="
				+ serverId + ", loginTime=" + loginTime + ", lastReadTime=" + lastReadTime + ", loginToken="
				+ loginToken + ", reToken=" + reToken + ", lastOffTime=" + lastOffTime + ", lastAddress=" + lastAddress
				+ ", connection=" + connection + "]";
	}
}
