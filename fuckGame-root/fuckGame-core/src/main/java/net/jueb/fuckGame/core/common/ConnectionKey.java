package net.jueb.fuckGame.core.common;

public interface ConnectionKey {
	
	/**
	 * 心跳序号
	 */
	public static final String HeartSeq="HeartSeq";
	/**
	 * 心跳最大关闭序号
	 */
	public static final String CloseMaxSeq="CloseMaxSeq";
	/**
	 * 最后异常读消息时间
	 */
	public static final String LastReadTimeMills="LastReadTimeMills";
}
