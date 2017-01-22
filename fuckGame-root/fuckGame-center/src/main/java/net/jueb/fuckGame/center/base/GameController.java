package net.jueb.fuckGame.center.base;

import java.util.HashMap;
import java.util.Map;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;

public class GameController implements Comparable<GameController>{
	
	private final ServerInfo serverInfo;
	private final NetConnection connection;
	private final Map<Integer,RoomInfo> rooms=new HashMap<Integer,RoomInfo>();
	
	public GameController(ServerInfo serverInfo, NetConnection connection){
		super();
		this.serverInfo = serverInfo;
		this.connection = connection;
	}

	public NetConnection getConnection() {
		return connection;
	}

	public Map<Integer, RoomInfo> getRooms() {
		return rooms;
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	@Override
	public String toString() {
		return "GameController [connection=" + connection + ", rooms=" + rooms + "]";
	}

	@Override
	public int compareTo(GameController o) {
		return this.getRooms().size()-o.getRooms().size();
	}
}