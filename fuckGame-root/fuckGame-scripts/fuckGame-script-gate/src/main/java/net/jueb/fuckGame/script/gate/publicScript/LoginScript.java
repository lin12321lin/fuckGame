package net.jueb.fuckGame.script.gate.publicScript;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import net.jueb.fuckGame.core.common.dto.RoleLoginRsp;
import net.jueb.fuckGame.core.common.dto.ServerResponse;
import net.jueb.fuckGame.core.common.dto.UserLoginInfo;
import net.jueb.fuckGame.core.common.enums.RoleGateEvent;
import net.jueb.fuckGame.core.common.enums.RoleSexEnum;
import net.jueb.fuckGame.core.net.NetConnection;
import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.net.message.GameErrCode;
import net.jueb.fuckGame.core.net.message.GameMessage;
import net.jueb.fuckGame.core.net.message.GameMsgCode;
import net.jueb.fuckGame.core.script.IServerScript;
import net.jueb.fuckGame.gate.ServerConfig;
import net.jueb.fuckGame.gate.ServerMain;
import net.jueb.fuckGame.gate.ServerQueue;
import net.jueb.fuckGame.gate.base.ConnectionKey;
import net.jueb.fuckGame.gate.base.GateLoginRsp;
import net.jueb.fuckGame.gate.base.RoleAgent;
import net.jueb.fuckGame.gate.factory.ScriptFactory;
import net.jueb.fuckGame.gate.manager.RoleAgentManager;
import net.jueb.util4j.cache.callBack.CallBack;
import net.jueb.util4j.cache.callBack.impl.AnnotationCallBackImpl;
import net.jueb.util4j.cache.callBack.impl.CallBackFunction;
import net.jueb.util4j.net.http.HttpUtil;
import net.sf.json.JSONObject;

/**
 * 用户登录
 * @author Administrator
 */
public class LoginScript extends AbstractPublicScript{

	@Override
	public void action() {
		
	}

	String req_reToken;
	String req_token;
	NetConnection conn;

	@Override
	protected void handleRequest(NetConnection connection, ByteBuffer clientBuffer) {
		if(connection.hasAttribute(ConnectionKey.RoleAgent))
		{
			RoleAgent ra=(RoleAgent) connection.getAttribute(ConnectionKey.RoleAgent);
			_log.error("重复登录操作:"+ra+",reqConnection:"+connection);
			return;
		}
		conn=connection;
		req_token=clientBuffer.readUTF();
		req_reToken=clientBuffer.readUTF();
		_log.debug("用户登录,token="+req_token+",req_reToken="+req_reToken);
		if(req_token==null)
		{
			response(GameErrCode.UnknownError);
			return ;
		}
		if(req_reToken!=null)
		{
			ra=RoleAgentManager.getInstance().findAgentByReToken(req_token);
			if(ra!=null)
			{
				NetConnection old=ra.getConnection();
				if(old!=null)
				{//关掉旧链接
					if(old!=conn)
					{
						_log.debug("玩家重登陆顶号:原链接="+old+",新链接="+conn);
						otherLoginDisconnect(ra.getRoleId(),ServerConfig.SERVER_ID,old, conn);
					}else
					{
						_log.error("玩家重复登陆操作在链路:"+conn);
					}
				}else
				{
					_log.debug("玩家重登陆在链路:"+conn);
				}
				RoleAgentManager.getInstance().addAgent(ra);//重新放入,免得过时移除
				ra.setReToken(buildReToken(ra));
				ra.setConnection(conn);
				conn.setAttribute(ConnectionKey.RoleAgent,ra);
				RoleAgent.totalReConnect.incrementAndGet();
				response(GameErrCode.Succeed);
				_log.debug("角色重连成功,刷新reToken="+req_token+",agent="+ra.toString());
				roleGateEvent(ra,RoleGateEvent.ReConnect);
				return ;
			}
		}
		UserLoginInfo login=null;
		if(req_token.startsWith("@"))
		{
			login=new  UserLoginInfo();
			login.setFaceIcon("http://xx.com/icon.png");
			login.setName("测试用户"+req_token);
			login.setUid(req_token);
			login.setSex(RoleSexEnum.male);
		}else
		{
			try {
				HttpUtil http=new HttpUtil();
				String url=ServerConfig.TOKEN_API+"?access_token="+req_token;
				byte[] data=http.httpPost(url, new byte[]{});
				JSONObject json=JSONObject.fromObject(new String(data,StandardCharsets.UTF_8));
				int resultcode=json.getInt("resultcode");
				if(resultcode!=1)
				{
					_log.error("token错误,resultcode="+resultcode);
					response(GameErrCode.WebTokenError);
					return ;
				}
				JSONObject result=json.getJSONObject("result");
				login=new  UserLoginInfo();
				login.setFaceIcon(result.getString("headUrl"));
				login.setName(result.getString("userName"));
				login.setUid(result.getString("userId"));
				int sex=result.getInt("sex");
				login.setSex(RoleSexEnum.valueOf(sex));
			} catch (Exception e) {
				_log.error(e.getMessage(),e);
			}
		}
		if(login==null || StringUtils.isEmpty(login.getUid()))
		{
			_log.error("用户uid参数获取失败:token="+req_token);
			response(GameErrCode.GateTokenError);
			return ;
		}
		login.setIp(connection.getIP());
		login.setToken(req_token);
		CallBack<ServerResponse<ByteBuffer>> call=new AnnotationCallBackImpl<ServerResponse<ByteBuffer>>(this, 1, 2);
		IServerScript script=ScriptFactory.getInstance().buildAction(GameMsgCode.Center_UserLogin,login,call);
		ServerMain.getInstance().getQueues().execute(ServerQueue.MAIN, script);
	}
	
	private String buildReToken(RoleAgent ra)
	{
		UUID uuid = UUID.randomUUID();
		return ra.getRoleId()+Long.toUnsignedString(uuid.getLeastSignificantBits(), Character.MAX_RADIX)+ServerConfig.SERVER_ID;
	}
	
	
	RoleAgent ra;
	@CallBackFunction(id=1)
	public void callLogin(ByteBuffer buffer)
	{
		GameErrCode code=GameErrCode.valueOf(buffer.readInt());
		if(code==GameErrCode.Succeed)
		{
			RoleLoginRsp rsp=new RoleLoginRsp();
			rsp.readFrom(buffer);
			long now=System.currentTimeMillis();
			ra=RoleAgentManager.getInstance().findAgent(rsp.getRoleId());
			if(ra!=null)
			{
				NetConnection old=ra.getConnection();
				if(old!=null)
				{//关掉旧链接
					if(old!=conn)
					{
						_log.debug("玩家重登陆顶号:原链接="+old+",新链接="+conn);
						otherLoginDisconnect(ra.getRoleId(),ServerConfig.SERVER_ID,old, conn);
					}else
					{
						_log.error("玩家重复登陆操作在链路:"+conn);
					}
				}else
				{
					_log.debug("玩家重登陆在链路:"+conn);
				}
			}else
			{
				ra=new RoleAgent();
			}
			String retoken=buildReToken(ra);
			GateLoginRsp vo=new GateLoginRsp();
			vo.setBag(rsp.getBagItem());
			vo.setRoleId(rsp.getRoleId());
			vo.setHeadIcon(rsp.getFaceIcon());
			vo.setMoney(rsp.getMoney());
			vo.setSex(rsp.getSex());
			vo.setRoleId(rsp.getRoleId());
			vo.setName(rsp.getName());
			ra.setLastRspInfo(vo);
			ra.setReToken(retoken);
			ra.setRoleId(rsp.getRoleId());
			ra.setRoleName(rsp.getName());
			ra.setServerId(rsp.getGameServerId());
			ra.setConnection(conn);
			ra.setLastReadTime(now);
			ra.setLastAddress(conn.getRemote());
			ra.setLoginTime(now);
			ra.setLoginToken(req_token);
			conn.setAttribute(ConnectionKey.RoleAgent,ra);
			RoleAgentManager.getInstance().addAgent(ra);
			_log.debug("网关登录成功"+ra);
			//网关角色锁定
			roleGateEvent(ra,RoleGateEvent.Login);
		}else
		{
			_log.error("网关登录失败,code="+code+",token="+req_token+",ip"+conn.getIP());
		}
		response(code);
	}
	
	@CallBackFunction(id=2)
	public void callLoginTimeOut()
	{
		response(GameErrCode.TimeOut);
		_log.error("网关登录超时"+req_token+",conn="+conn);
	}

	@Override
	public int getMessageCode() {
		return GameMsgCode.Gate_UserLogin;
	}
	
	/**
	 * 回复
	 * @param code
	 */
	protected void response(GameErrCode code)
	{
		ByteBuffer buffer=new ByteBuffer();
		buffer.writeInt(code.value());
		if(code==GameErrCode.Succeed)
		{
			buffer.writeUTF(ra.getReToken());
			GateLoginRsp rsp=ra.getLastRspInfo();
			rsp.writeTo(buffer);
		}
		conn.sendMessage(new GameMessage(getMessageCode(),buffer));
	}
}
