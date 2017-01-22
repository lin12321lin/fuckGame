package net.jueb.fuckGame.game.base;

import java.util.HashMap;
import java.util.Map;

public class GameRooms {
		
	private int id;
	private final Map<Integer,GameRoom> rooms=new HashMap<Integer,GameRoom>();
	
	public GameRooms(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Map<Integer, GameRoom> getRooms() {
		return rooms;
	}

	@Override
	public String toString() {
		return "GameRooms [id=" + id + ", rooms=" + rooms + "]";
	}
}