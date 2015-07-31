package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.util.ResourceUtil;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * User: pauls
 * Timestamp: 6/01/14 1:41 PM
 */
public class ShaderObject {
	private static final Logger logger = LoggerFactory.getLogger(ShaderObject.class);

	private boolean isInitialised;
	private boolean isCompiled;
	private int glObjectIdx;
	private final ShaderObjectType type;

	public static enum ShaderObjectType {
		VertexShader(GL3.GL_VERTEX_SHADER, "Vertex Shader"),
		GeometryShader(GL3.GL_GEOMETRY_SHADER, "Geometry Shader"),
		FragmentShader(GL3.GL_FRAGMENT_SHADER, "Fragment Shader");

		public final int glType;
		public final String name;

		private ShaderObjectType(int glType, String name) {
			this.glType = glType;
			this.name = name;
		}
	}

	public ShaderObject(ShaderObjectType type) {
		isInitialised = false;
		isCompiled = false;
		glObjectIdx = -1;
		this.type = type;
	}


	public void initalise(GL3 gl) {
		if (isInitialised) {
			logger.warn("Reinitialised " + type.name + " Object that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			glObjectIdx = gl.glCreateShader(type.glType);
			if (glObjectIdx <= 0) {
				logger.error("Failed to create " + type.name + " object");
				glObjectIdx = -1;
				isInitialised = false;
				return;
			}

			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			gl.glDeleteShader(glObjectIdx);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	public boolean compileShaderFromResource(GL3 gl, String resourceLocation) {
		try {
			return compileShaderCode(gl, ResourceUtil.stringFromResource(resourceLocation));
		} catch(IOException ex) {
			logger.warn("Failed to compile " + resourceLocation, ex);
			return false;
		}
	}

	public boolean compileShaderCode(GL3 gl, String shaderCode) {
		if (!isInitialised) {
			logger.error("Attempted to compile Shader Code on an uninitalised " + type.name + " Object");
			return false;
		}

		if (isCompiled)
			logger.warn("Recompiling shader object that is already compiled");

		String[] sources = new String[] {shaderCode};
		int[] sourceLengths = new int[sources.length];
		for (int i = 0; i < sourceLengths.length; i++) {
			sourceLengths[i] = sources[i].length();
		}

		gl.glShaderSource(
				glObjectIdx, // gl shader object
				sources.length, // count
				sources, // shader source[]
				sourceLengths, // shader source length[]
				0);				// shader source/length[] offset

		gl.glCompileShader(glObjectIdx);

		IntBuffer ibuf = ByteBuffer.allocateDirect(1 * Buffers.SIZEOF_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
		gl.glGetShaderiv(glObjectIdx, GL3.GL_COMPILE_STATUS, ibuf);
		if (ibuf.get(0) == GL3.GL_FALSE) {
			gl.glGetShaderiv(glObjectIdx, GL3.GL_INFO_LOG_LENGTH, ibuf);
			String out = null;
			int length = ibuf.get(0);
			if (length > 0) {
				final ByteBuffer infoLog = ByteBuffer.allocateDirect(length);
				gl.glGetShaderInfoLog(glObjectIdx, infoLog.limit(), null, infoLog);
				final byte[] infoBytes = new byte[length];
				infoLog.get(infoBytes);
				out = new String(infoBytes);
			}
			logger.error(type.name + " Object failed to compile, " + out);
			isCompiled = false;
		} else {
			isCompiled = true;
		}
		return isCompiled;
	}

	public boolean isCompiled() {
		return isCompiled;
	}

	protected void attachToShaderProgram(GL3 gl, int shaderProgramGlIdx) {
		if (!isInitialised) {
			logger.error("Attempted to attach uninitialised " + type.name + " Object to a Shader Program");
			return;
		}
		if (!isCompiled) {
			logger.error("Attempted to attach uncompiled " + type.name + " Object to a Shader Program");
			return;
		}
		gl.glAttachShader(shaderProgramGlIdx, glObjectIdx);
	}
}
