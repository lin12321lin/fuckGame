package net.jueb.fuckGame.core.common.dto.def;

import net.jueb.fuckGame.core.net.message.ByteBuffer;
import net.jueb.fuckGame.core.util.Dto;

public class Def_Item implements Dto{

	/**
	 * id
	 */
	private int id;
	/**
	 * 名称
	 */
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void readFrom(ByteBuffer buffer) {
		id=buffer.readInt();
		name=buffer.readUTF();
	}

	@Override
	public void writeTo(ByteBuffer buffer) {
		buffer.writeInt(id);
		buffer.writeUTF(name);
	}

	@Override
	public String toString() {
		return "Def_Item [id=" + id + ", name=" + name + "]";
	}
}
