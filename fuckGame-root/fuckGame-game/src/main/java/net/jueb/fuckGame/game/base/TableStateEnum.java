package net.jueb.fuckGame.game.base;
/**
 * 桌子状态
 */
public enum TableStateEnum {
	/**
	 *已创建(玩家人数未齐)
	 */
	Created(0),
	/**
	 * 就绪中,准备开始游戏(此状态玩家人数已齐)
	 */
	Ready(1),
	/**
	 * 游戏中
	 */
	Gameing(2),
	/**
	 * 游戏结束
	 */
	GameOver(3),
	;
	private int value;
	private long stateTime;

	private TableStateEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public long getStateTime() {
		return stateTime;
	}

	public void setStateTime(long stateTime) {
		this.stateTime = stateTime;
	}

	public static final TableStateEnum valueOf(int value) {
		for (TableStateEnum am : TableStateEnum.values())
			if (am.getValue() == value)
				return am;
		return null;
	}
}