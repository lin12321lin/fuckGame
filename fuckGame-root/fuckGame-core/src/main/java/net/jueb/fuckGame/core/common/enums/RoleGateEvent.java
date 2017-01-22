package net.jueb.fuckGame.core.common.enums;

public enum RoleGateEvent {
    /**
     *登录
     */
    Login(0),
    /**
     *断线
     */
    Disconnect(1),
    /**
     *重连
     */
    ReConnect(2),
    /**
     *断线未重连,网关移除
     */
    LogOut(3),
    ;

	private final int value;
	
	private RoleGateEvent(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static final RoleGateEvent valueOf(int value) {
		for (RoleGateEvent am : RoleGateEvent.values())
			if (am.getValue() == value)
				return am;
		return null;
	}
}