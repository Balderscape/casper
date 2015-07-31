package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.buffer.BufferType;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: pauls
 * Timestamp: 15/01/14 4:43 PM
 *
 * Define the Uniform blocks in glsl to use std140 layout as follows: layout(std140) uniform;
 *
 * NOTE: All fields in a block must be 16 byte aligned which means if you have a vec3 followed by mat4 then the mat4
 * will start at vec3 + 16, NOT vec3 + 12.
 */
public abstract class UniformBlock {
	private static final Logger logger = LoggerFactory.getLogger(UniformBlock.class);
	private static final AtomicInteger NEXT_UNIFORM_BLOCK_INDEX = new AtomicInteger();

	String name;
	final int uniformBlockIndex;

	private int glObjectIdx = -1;
	boolean isInitialised = false;
	private BufferType bufferType;

	protected ByteBuffer byteBuffer;
	protected int size;
	private boolean sizeChanged = false;
	protected boolean isDirty = false;

	public UniformBlock(String name, int size, BufferType bufferType) {
		this.name = name;
		this.uniformBlockIndex = NEXT_UNIFORM_BLOCK_INDEX.getAndIncrement();
		this.bufferType = bufferType;
		if (size > 0) setSize(size);

	}

	public void initalise(GL3 gl) {
		if (isInitialised) {
			logger.warn("Reinitialised Uniform Buffer that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			int[] arg = new int[1];
			gl.glGenBuffers(1, arg, 0);
			glObjectIdx = arg[0];
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			gl.glBindBufferBase(GL3.GL_UNIFORM_BUFFER, uniformBlockIndex, 0);
			int[] arg = {glObjectIdx};
			gl.glDeleteBuffers(1, arg, 0);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	protected void setSize(int size) {
		this.size = size;
		this.byteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		sizeChanged = true;
	}

	public void clean(GL3 gl) {
		if(sizeChanged) {
			gl.glBindBuffer(GL3.GL_UNIFORM_BUFFER, glObjectIdx);
			gl.glBufferData(GL3.GL_UNIFORM_BUFFER, size, null, bufferType.glType);
			gl.glBindBufferRange(GL3.GL_UNIFORM_BUFFER, uniformBlockIndex, glObjectIdx, 0, size);
			gl.glBindBuffer(GL3.GL_UNIFORM_BUFFER, 0);
			sizeChanged = false;
		}

		if (isDirty) {
			gl.glBindBuffer(GL3.GL_UNIFORM_BUFFER, glObjectIdx);
			gl.glBufferSubData(GL3.GL_UNIFORM_BUFFER, 0, size, byteBuffer.rewind());
			gl.glBindBuffer(GL3.GL_UNIFORM_BUFFER, 0);
			isDirty = false;
		}
	}

	public abstract void update(Scene scene);
}
