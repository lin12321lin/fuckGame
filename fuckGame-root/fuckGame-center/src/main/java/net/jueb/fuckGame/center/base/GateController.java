package net.jueb.fuckGame.center.base;

import net.jueb.fuckGame.core.common.ServerInfo;
import net.jueb.fuckGame.core.net.NetConnection;

public class GateController {
		
	private final ServerInfo gateInfo;
	private final NetConnection connection;
		
	public GateController(ServerInfo gateInfo, NetConnection connection) {
		super();
		this.gateInfo = gateInfo;
		this.connection = connection;
	}
	public ServerInfo getGateInfo() {
		return gateInfo;
	}
	public NetConnection getConnection() {
		return connection;
	}
	@Override
	public String toString() {
		return "GateClient [gateInfo=" + gateInfo + ", connection=" + connection + "]";
	}
}