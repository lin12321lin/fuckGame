package net.jueb.fuckGame.game.base;
/**
 *		   北(3)
 * 西(2)	     	东(0)
 * 		    南(1)
 * @author jaci
 */
public enum TableLocationEnum {
	/**
	 * 东
	 */
	East(0),
	/**
	 * 南
	 */
	South(1),
	/**
	 * 西
	 */
	West(2),
	/**
	 * 北
	 */
	North(3),
	;
	private int value;

	private TableLocationEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static final TableLocationEnum valueOf(int value) {
		for (TableLocationEnum am : TableLocationEnum.values())
			if (am.getValue() == value)
				return am;
		return null;
	}
}