package net.jueb.fuckGame.center;

public class ServerQueue {

	public final static short IO_EVENT = 1;
	
	/** 主队列索引 */
	public final static short MAIN = 2;
	
	/**
	 *日志
	 */
	public final static short LOG = 3;
	
	/**
	 * 登录队列
	 * 缓存没有取队列
	 */
	public final static short LOGIN = 4;
	
	/**
	 * 注册队列
	 */
	public final static short REG = 5;
	
	/**
	 * 数据保存队列
	 */
	public final static short DATA_SAVE = 6;
	
	/**
	 * 调试队列
	 */
	public final static short DEBUG = 7;
}
