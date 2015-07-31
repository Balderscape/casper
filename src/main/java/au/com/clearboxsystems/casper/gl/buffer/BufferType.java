package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import com.jogamp.opengl.GL3;

/**
 * User: pauls
 * Timestamp: 16/01/14 1:10 PM
 */
public enum BufferType {
	StaticDraw(GL3.GL_STATIC_DRAW),
	StaticRead(GL3.GL_STATIC_READ),
	StaticCopy(GL3.GL_STATIC_COPY),
	DynamicDraw(GL3.GL_DYNAMIC_DRAW),
	DynamicRead(GL3.GL_DYNAMIC_READ),
	DynamicCopy(GL3.GL_DYNAMIC_COPY);

	public final int glType;

	private BufferType(int glType) {
		this.glType = glType;
	}
}
