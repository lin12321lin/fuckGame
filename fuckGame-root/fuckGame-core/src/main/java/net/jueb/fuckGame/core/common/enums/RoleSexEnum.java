package net.jueb.fuckGame.core.common.enums;

public enum RoleSexEnum {
	/**
	 * 未定义
	 */
	undefine(-1),
	/**
	 * 男士
	 */
	male(0),
	/**
	 * 女士
	 */
	female(1),;
	private int value;

	private RoleSexEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static final RoleSexEnum valueOf(int value) {
		for (RoleSexEnum am : RoleSexEnum.values())
			if (am.getValue() == value)
				return am;
		return RoleSexEnum.undefine;
	}
}