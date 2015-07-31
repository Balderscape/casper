package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: pauls
 * Timestamp: 6/01/14 2:19 PM
 */
public class RenderBuffer {
	private static final Logger logger = LoggerFactory.getLogger(RenderBuffer.class);

	private boolean isInitialised;
	private int glObjectIdx;
	private RenderBufferType type;

	public enum RenderBufferType {
		// Unsigned Normalized
		Red8(GL3.GL_R8),
		Red16(GL3.GL_R16),
		RG8(GL3.GL_RG8),
		RG16(GL3.GL_RG16),
		RGBA8(GL3.GL_RGBA8),
		RGBA16(GL3.GL_RGBA16),
		// Float
		Red16f(GL3.GL_R16F),
		RG16f(GL3.GL_RG16F),
		RGBA16f(GL3.GL_RGBA16F),
		Red32f(GL3.GL_R32F),
		RG32f(GL3.GL_RG32F),
		RGBA32f(GL3.GL_RGBA32F),
		// Signed Integral
		Red8i(GL3.GL_R8I),
		RG8i(GL3.GL_RG8I),
		RGBA8i(GL3.GL_RGBA8I),
		Red16i(GL3.GL_R16I),
		RG16i(GL3.GL_RG16I),
		RGBA16i(GL3.GL_RGBA16I),
		Red32i(GL3.GL_R32I),
		RG32i(GL3.GL_RG32I),
		RGBA32i(GL3.GL_RGBA32I),
		// Unsigned Integral
		Red8ui(GL3.GL_R8UI),
		RG8ui(GL3.GL_RG8UI),
		RGBA8ui(GL3.GL_RGBA8UI),
		Red16ui(GL3.GL_R16UI),
		RG16ui(GL3.GL_RG16UI),
		RGBA16ui(GL3.GL_RGBA16UI),
		Red32ui(GL3.GL_R32UI),
		RG32ui(GL3.GL_RG32UI),
		RGBA32ui(GL3.GL_RGBA32UI),
		// Depth Buffer
		DepthComponent16(GL3.GL_DEPTH_COMPONENT16),
		DepthComponent24(GL3.GL_DEPTH_COMPONENT24),
		DepthComponent32(GL3.GL_DEPTH_COMPONENT32),
		DepthComponent32f(GL3.GL_DEPTH_COMPONENT32F),
		DepthComponent24Stencil8(GL3.GL_DEPTH24_STENCIL8),
		DepthComponent32fStencil8(GL3.GL_DEPTH32F_STENCIL8);

		public final int glType;

		private RenderBufferType(int glType) {
			this.glType = glType;
		}
	}

	public RenderBuffer() {
		isInitialised = false;
		glObjectIdx = -1;
	}

	public void initalise(GL3 gl, RenderBufferType type, int width, int height) {
		if (isInitialised) {
			logger.warn("Reinitialised Render Buffer that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			int [] arg = new int[1];
			gl.glGenRenderbuffers(1, arg, 0);
			glObjectIdx = arg[0];
			gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, glObjectIdx);
			gl.glRenderbufferStorage(GL3.GL_RENDERBUFFER, type.glType, width, height);
			this.type = type;
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			int [] arg = {glObjectIdx};
			gl.glDeleteRenderbuffers(1, arg, 0);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	public void bind(GL3 gl) {
		if (!isInitialised) {
			logger.error("Attempted to bind an uninitalised Vertex Buffer");
			return;
		}
		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, glObjectIdx);
	}

	public static void unbind(GL3 gl) {
		gl.glBindRenderbuffer(GL3.GL_RENDERBUFFER, 0);
	}

	protected int getGLHandle() {
		return glObjectIdx;
	}

}
