package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.Vector2;
import au.com.clearboxsystems.casper.math.Vector3;
import au.com.clearboxsystems.casper.math.Vector4;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:14 PM
 */
public class VertexBuffer {
	private static final Logger logger = LoggerFactory.getLogger(VertexBuffer.class);

	private boolean isInitialised;
	private int glObjectIdx;
	private boolean isDirty = false;
	private int count;
	private boolean isLoading = false;
	private ByteBuffer loadingBuffer;
	private ByteBuffer completeBuffer;
	private BufferType bufferType;
	private int stride;

	public VertexBuffer(BufferType bufferType) {
		isInitialised = false;
		this.bufferType = bufferType;
		glObjectIdx = -1;
		stride = 4;
	}

	public VertexBuffer(BufferType bufferType, int stride) {
		isInitialised = false;
		this.bufferType = bufferType;
		glObjectIdx = -1;
		this.stride = stride;
	}

	public void initalise(GL3 gl) {
		if (isInitialised) {
			logger.warn("Reinitialised Vertex Buffer that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			int[] arg = new int[1];
			gl.glGenBuffers(1, arg, 0);
			glObjectIdx = arg[0];
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			int[] arg = {glObjectIdx};
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
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, glObjectIdx);
	}

	public static void unbind(GL3 gl) {
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
	}

	public void add(int f) {
		if (!isLoading) {
			logger.warn("Add called when not in loading mode. Call startLoading to assign a buffer to load verticies into");
			return;
		}
		if (loadingBuffer.remaining() < Buffers.SIZEOF_INT) {
			logger.warn("Vertex buffer was incorrectly size, growing buffer from " + loadingBuffer.limit() + " to " + loadingBuffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(loadingBuffer.limit() * 2).order(ByteOrder.nativeOrder());
			loadingBuffer.flip();
			newBuf.put(loadingBuffer);
			loadingBuffer = newBuf;
		}
		loadingBuffer.putInt(f);
	}

	public void add(float f) {
		if (!isLoading) {
			logger.warn("Add called when not in loading mode. Call startLoading to assign a buffer to load verticies into");
			return;
		}
		if (loadingBuffer.remaining() < Buffers.SIZEOF_FLOAT) {
			logger.warn("Vertex buffer was incorrectly size, growing buffer from " + loadingBuffer.limit() + " to " + loadingBuffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(loadingBuffer.limit() * 2).order(ByteOrder.nativeOrder());
			loadingBuffer.flip();
			newBuf.put(loadingBuffer);
			loadingBuffer = newBuf;
		}
		loadingBuffer.putFloat(f);
	}

	public void add(Vector2 vec) {
		if (!isLoading) {
			logger.warn("Add called when not in loading mode. Call startLoading to assign a buffer to load verticies into");
			return;
		}
		if (loadingBuffer.remaining() < 2 * Buffers.SIZEOF_FLOAT) {
			logger.warn("Vertex buffer was incorrectly size, growing buffer from " + loadingBuffer.limit() + " to " + loadingBuffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(loadingBuffer.limit() * 2).order(ByteOrder.nativeOrder());
			loadingBuffer.flip();
			newBuf.put(loadingBuffer);
			loadingBuffer = newBuf;
		}
		loadingBuffer.putFloat((float) vec.x);
		loadingBuffer.putFloat((float) vec.y);
	}

	public void add(Vector3 vec) {
		if (!isLoading) {
			logger.warn("Add called when not in loading mode. Call startLoading to assign a buffer to load verticies into");
			return;
		}
		if (loadingBuffer.remaining() < 3 * Buffers.SIZEOF_FLOAT) {
			logger.warn("Vertex buffer was incorrectly size, growing buffer from " + loadingBuffer.limit() + " to " + loadingBuffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(loadingBuffer.limit() * 2).order(ByteOrder.nativeOrder());
			loadingBuffer.flip();
			newBuf.put(loadingBuffer);
			loadingBuffer = newBuf;
		}
		loadingBuffer.putFloat((float) vec.x);
		loadingBuffer.putFloat((float) vec.y);
		loadingBuffer.putFloat((float) vec.z);
	}

	public void add(Vector4 vec) {
		if (!isLoading) {
			logger.warn("Add called when not in loading mode. Call startLoading to assign a buffer to load verticies into");
			return;
		}
		if (loadingBuffer.remaining() < 4 * Buffers.SIZEOF_FLOAT) {
			logger.warn("Vertex buffer was incorrectly size, growing buffer from " + loadingBuffer.limit() + " to " + loadingBuffer.limit() * 2 + " bytes. This is not a problem but may have a performance impact if it happens alot");
			ByteBuffer newBuf = ByteBuffer.allocateDirect(loadingBuffer.limit() * 2).order(ByteOrder.nativeOrder());
			loadingBuffer.flip();
			newBuf.put(loadingBuffer);
			loadingBuffer = newBuf;
		}
		loadingBuffer.putFloat((float) vec.x);
		loadingBuffer.putFloat((float) vec.y);
		loadingBuffer.putFloat((float) vec.z);
		loadingBuffer.putFloat((float) vec.w);
	}

	public void startLoading(int numBytes) {
		loadingBuffer = ByteBuffer.allocateDirect(numBytes).order(ByteOrder.nativeOrder());
		isLoading = true;
	}

	public void loadBuffer(ByteBuffer buffer) {
		isLoading = false;
		loadingBuffer = null;
		completeBuffer = buffer;
		isDirty = true;
	}

	/**
	 * When vertices are added to the vertex buffer it goes into the loading state, and
	 * vertices will not be flushed to the GPU until the load complete is set.
	 */
	public void finishLoading() {
		isLoading = false;
		completeBuffer = loadingBuffer;
		loadingBuffer = null;
		isDirty = true;
	}

	public void clean(GL3 gl) {
		if (!isDirty) {
			return;
		}

		if (!isInitialised) {
			logger.error("Attempted to clean an uninitalised Vertex Buffer");
			return;
		}

		int size = completeBuffer.position();
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, glObjectIdx);
		if (size != count * stride)
			gl.glBufferData(GL3.GL_ARRAY_BUFFER, size, completeBuffer.rewind(), bufferType.glType);
		else
			gl.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, size, completeBuffer.rewind());
		count = size / stride;
		isDirty = false;
	}

	public int getCount() {
		return count;
	}

	/**
	 * Accesses the loading buffer so that vertices can be read back during buffer creation
	 * Note: This will not be available after a finishLoding is used.
	 */
	public Vector3 getVertex(int i) {
		if (!isLoading)
			return null;

		Vector3 vector = new Vector3();
		vector.x = loadingBuffer.getFloat(Buffers.SIZEOF_FLOAT * (3 * i));
		vector.y = loadingBuffer.getFloat(Buffers.SIZEOF_FLOAT * (3 * i + 1));
		vector.z = loadingBuffer.getFloat(Buffers.SIZEOF_FLOAT * (3 * i + 2));

		return vector;
	}
}
