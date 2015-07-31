package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shader.Attribute;
import au.com.clearboxsystems.casper.gl.shader.Shader;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:17 PM
 */
public class VertexArray {
	private static final Logger logger = LoggerFactory.getLogger(VertexArray.class);

	private boolean isInitialised;
	private int glObjectIdx;

	private IndexBuffer indexBuffer;
	private boolean isIndexBufferBound = false;
	private ArrayList<VertexBufferLink> vertexBuffers = new ArrayList<VertexBufferLink>();
	private boolean isDirty = false;

	private int _maxIndex;

	public VertexArray() {
		isInitialised = false;
		glObjectIdx = -1;
	}

	public void initalise(GL3 gl) {
		if (isInitialised) {
			logger.warn("Reinitialised Vertex Array that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			int [] arg = new int[1];
			gl.glGenVertexArrays(1, arg, 0);
			glObjectIdx = arg[0];
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			int [] arg = {glObjectIdx};
			gl.glDeleteVertexArrays(1, arg, 0);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	public void bind(GL3 gl) {
		if (!isInitialised) {
			logger.error("Attempted to bind an uninitalised Vertex Array");
			return;
		}
		gl.glBindVertexArray(glObjectIdx);
	}

	public static void unbind(GL3 gl) {
		gl.glBindVertexArray(0);
	}

	public void attachIndexBuffer(IndexBuffer indexBuffer) {
		this.indexBuffer = indexBuffer;
	}

	public void attachVertexBuffer(VertexBuffer vertexBuffer, String attributeName, int numComponents) {
		VertexBufferLink link = new VertexBufferLink(vertexBuffer, attributeName, numComponents);
		vertexBuffers.add(link);
		isDirty = true;
	}

	public void attachVertexBuffer(VertexBuffer vertexBuffer, String attributeName, int numComponents, int stride, int offset, int glType) {
		VertexBufferLink link = new VertexBufferLink(vertexBuffer, attributeName, numComponents, stride, offset, glType);
		vertexBuffers.add(link);
		isDirty = true;
	}

	/**
	 * Cleans the Vertex array by flushing any cached state to the GPU
	 *
	 * @param gl
	 * @param shader
	 */
	public void clean(GL3 gl, Shader shader) {
		if (indexBuffer != null) {
			indexBuffer.clean(gl);
			if (!isIndexBufferBound)
				indexBuffer.bind(gl);
		}

		_maxIndex = 0;
		for (VertexBufferLink link : vertexBuffers) {
			link.vertexBuffer.clean(gl);
			int maxIdx = link.vertexBuffer.getCount() - 1;
			if (maxIdx > _maxIndex)
				_maxIndex = maxIdx;
		}
		if (isDirty) {
			for (VertexBufferLink link : vertexBuffers) {
				if (link.dirty) {
					Attribute attribute = shader.getAttribute(link.attributeName);
					if (attribute == null)
						logger.error(link.attributeName + " does not exist in shader program");
					else {
						gl.glEnableVertexAttribArray(attribute.location);
						link.vertexBuffer.bind(gl);
						gl.glVertexAttribPointer(
								attribute.location,			// Index
								link.numComponents,			// Size
								link.glType,				// Component
								false,						// Normalise
								link.stride,				// Stride
								link.offset);				// buff offset
					}
					link.dirty = false;
				}
			}
			isDirty = false;
		}
	}

	public int getMaxIndex() {
		return _maxIndex;
	}

	private class VertexBufferLink {
		public final VertexBuffer vertexBuffer;
		public final String attributeName;
		public final int numComponents;
		public boolean dirty;

		public int stride;
		public int offset;
		public int glType;

		public VertexBufferLink(VertexBuffer vertexBuffer, String attributeName, int numComponents) {
			this.vertexBuffer = vertexBuffer;
			this.attributeName = attributeName;
			this.numComponents = numComponents;
			this.dirty = true;

			stride = 0;
			offset = 0;
			glType = GL3.GL_FLOAT;
		}

		public VertexBufferLink(VertexBuffer vertexBuffer, String attributeName, int numComponents, int stride, int offset, int glType) {
			this.vertexBuffer = vertexBuffer;
			this.attributeName = attributeName;
			this.numComponents = numComponents;
			this.dirty = true;

			this.stride = stride;
			this.offset = offset;
			this.glType = glType;
		}
	}
}
