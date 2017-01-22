package net.jueb.fuckGame.game.base;
/**
 * 房间消息类型
 */
public enum RoomMsgTypeEnum {
	/**
	 *文本消息
	 */
	Text(0),
	/**
	 *语音标签
	 */
	Audio_Tag(1),
	/**
	 * 二进制
	 */
	Binnary(2),
	;
	private int value;

	private RoomMsgTypeEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static final RoomMsgTypeEnum valueOf(int value) {
		for (RoomMsgTypeEnum am : RoomMsgTypeEnum.values())
			if (am.getValue() == value)
				return am;
		return null;
	}
}