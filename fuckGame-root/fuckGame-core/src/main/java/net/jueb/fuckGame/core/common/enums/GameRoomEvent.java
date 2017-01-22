package net.jueb.fuckGame.core.common.enums;

public enum GameRoomEvent {
	Entry(0),
    Create(1),
    Exit(2),
    Destroy(3),
    ;

	private final int value;
	
	private GameRoomEvent(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static final GameRoomEvent valueOf(int value) {
		for (GameRoomEvent am : GameRoomEvent.values())
			if (am.getValue() == value)
				return am;
		return null;
	}
}