package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:13 PM
 */
public class IndexBuffer {
	private static final Logger logger = LoggerFactory.getLogger(IndexBuffer.class);


	private boolean isInitialised;
	private int glObjectIdx;

	private ByteBuffer buffer;
	private boolean isDirty = false;

	private int count;
	private int type = GL3.GL_UNSIGNED_INT;

	public IndexBuffer(int numBytes) {
		isInitialised = false;
		glObjectIdx = -1;
		buffer = ByteBuffer.allocateDirect(numBytes).order(ByteOrder.nativeOrder());
	}

	public void initalise(GL3 gl) {
		if (isInitialised) {
			logger.warn("Reinitialised Index Buffer that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			int [] arg = new int[1];
			gl.glGenBuffers(1, arg, 0);
			glObjectIdx = arg[0];
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			int [] arg = {glObjectIdx};
			gl.glDeleteBuffers(1, arg, 0);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	public void bind(GL3 gl) {
		if (!isInitialised) {
			logger.error("Attempted to bind an uninitalised Vertex Buffer");
			return;
		}
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, glObjectIdx);
	}

	public static void unbind(GL3 gl) {
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void addTri(int a, int b, int c) {
		if (buffer.remaining() < 3 * Buffers.SIZEOF_INT) {
			logger.warn("Index buffer was incorrectly size, growing buffer from " + buffer.limit() + " to " + buffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(buffer.limit() * 2).order(ByteOrder.nativeOrder());
			buffer.flip();
			newBuf.put(buffer);
			buffer = newBuf;
		}
		buffer.putInt(a);
		buffer.putInt(b);
		buffer.putInt(c);
		isDirty = true;
	}

	public void add(int i) {
		if (buffer.remaining() < Buffers.SIZEOF_INT) {
			logger.warn("Index buffer was incorrectly size, growing buffer from " + buffer.limit() + " to " + buffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(buffer.limit() * 2).order(ByteOrder.nativeOrder());
			buffer.flip();
			newBuf.put(buffer);
			buffer = newBuf;
		}
		buffer.putInt(i);
		isDirty = true;
	}
	public void clean(GL3 gl) {
		if (!isDirty)
			return;

		if (!isInitialised) {
			logger.error("Attempted to clean an uninitalised Index Buffer");
			return;
		}

		int size = buffer.position();
//		gl.glBindVertexArray(0);
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, glObjectIdx);
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, size, buffer.rewind(), GL3.GL_STATIC_DRAW);
		count = size / Buffers.SIZEOF_INT;
		isDirty = false;
	}

	public int getCount() {
		return count;
	}

	public int getGLType() {
		return type;
	}

}
