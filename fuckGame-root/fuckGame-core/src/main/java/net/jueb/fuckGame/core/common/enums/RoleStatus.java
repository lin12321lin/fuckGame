package net.jueb.fuckGame.core.common.enums;

public enum RoleStatus {

	/**
	 *启用
	 */
	Enable(1),
	/**
	 * 禁用
	 */
	Disable(2),
	;
	
	private int value;
	private RoleStatus(int value) {
		this.value=value;
	}
	public int value()
	{
		return this.value;
	}
	
	public static RoleStatus valueOf(int value)
	{
		for(RoleStatus gt:values())
		{
			if(gt.value==value)
			{
				return gt;
			}
		}
		return null;
	}
}
