package net.jueb.fuckGame.core.util;

import net.jueb.fuckGame.core.net.message.ByteBuffer;

public interface Dto {

	public void readFrom(ByteBuffer buffer);
	
	public void writeTo(ByteBuffer buffer);
}
