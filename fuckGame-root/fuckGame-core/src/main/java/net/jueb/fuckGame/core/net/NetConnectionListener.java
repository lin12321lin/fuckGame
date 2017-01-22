package net.jueb.fuckGame.core.net;

public interface NetConnectionListener<T>{
	
	public abstract void messageArrived(NetConnection conn,T msg);
    
	public abstract void connectionOpened(NetConnection connection);

	public abstract void connectionClosed(NetConnection connection);
}
