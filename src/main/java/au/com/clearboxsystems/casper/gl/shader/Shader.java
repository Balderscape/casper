package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shape.Shape;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * User: pauls
 * Timestamp: 6/01/14 1:57 PM
 */
public class Shader {

	private static final Logger logger = LoggerFactory.getLogger(Shader.class);

	private int glObjectIdx;
	private boolean isInitialised;
	private boolean isLinked;

	private Map<String, Attribute> shaderAttributes = new HashMap<String, Attribute>();
	private Map<String, Uniform> shaderUniforms = new HashMap<String, Uniform>();

	public Shader() {
		glObjectIdx = -1;
		isInitialised = false;
		isLinked = false;
	}

	public void initialise(GL3 gl) {
		if(isInitialised){
			logger.warn("Reinitialised Shader Program. This will cause a memory leak in GPU");
		} else {
			glObjectIdx = gl.glCreateProgram();
			if (glObjectIdx <= 0){
				logger.error("failed to create Shader Program");
				glObjectIdx = -1;
				isInitialised = false;
				return;
			}
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if(isInitialised){
			gl.glDeleteProgram(glObjectIdx);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	public boolean compileAndLinkShaderProgramFromResource(GL3 gl, String vertexShaderResource, String fragmentShaderResource) {
		return compileAndLinkShaderProgramFromResource(gl, vertexShaderResource, null, fragmentShaderResource);
	}

	public boolean compileAndLinkShaderProgramFromResource(GL3 gl, String vertexShaderResource, String geometryShaderResource, String fragmentShaderResource) {
		ShaderObject vertexShader = new ShaderObject(ShaderObject.ShaderObjectType.VertexShader);
		vertexShader.initalise(gl);
		if (!vertexShader.compileShaderFromResource(gl, vertexShaderResource))
			return false;

		ShaderObject geometryShader = null;
		if (geometryShaderResource != null) {
			geometryShader = new ShaderObject(ShaderObject.ShaderObjectType.GeometryShader);
			geometryShader.initalise(gl);
			if (!geometryShader.compileShaderFromResource(gl, geometryShaderResource))
				return false;
		}

		ShaderObject fragmentShader = new ShaderObject(ShaderObject.ShaderObjectType.FragmentShader);
		fragmentShader.initalise(gl);
		if(!fragmentShader.compileShaderFromResource(gl, fragmentShaderResource))
			return false;

		return linkShaderProgram(gl, vertexShader, geometryShader, fragmentShader);
	}

	public boolean linkShaderProgram(GL3 gl, ShaderObject vertexShader, ShaderObject fragmentShader) {
		return linkShaderProgram(gl, vertexShader, null, fragmentShader);
	}

	public boolean linkShaderProgram(GL3 gl, ShaderObject vertexShader, ShaderObject geometryShader, ShaderObject fragmentShader) {
		if (isLinked) {
			logger.warn("Relinking shader program that is already linked");
		}

		if (vertexShader == null) {
			logger.error("Vertex Shader set to null");
			return false;
		}
		if (fragmentShader == null) {
			logger.error("Fragment Shader set to null");
			return false;
		}
		if (!vertexShader.isCompiled()) {
			logger.error("Vertex Shader is not compiled");
			return false;
		}
		if (geometryShader != null && !geometryShader.isCompiled()) {
			logger.error("Geometry Shader is not compiled");
			return false;
		}
		if (!fragmentShader.isCompiled()) {
			logger.error("Fragment Shader is not compiled");
			return false;
		}

		vertexShader.attachToShaderProgram(gl, glObjectIdx);
		if (geometryShader != null) {
			geometryShader.attachToShaderProgram(gl, glObjectIdx);
		}
		fragmentShader.attachToShaderProgram(gl, glObjectIdx);

		gl.glLinkProgram(glObjectIdx);

		IntBuffer ibuf = ByteBuffer.allocateDirect(1 * Buffers.SIZEOF_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
		gl.glGetProgramiv(glObjectIdx, GL3.GL_LINK_STATUS, ibuf);
		if (ibuf.get(0) == GL3.GL_FALSE) {
			gl.glGetProgramiv(glObjectIdx, GL3.GL_INFO_LOG_LENGTH, ibuf);
			String out = null;
			int length = ibuf.get(0);
			if (length > 0) {
				final ByteBuffer infoLog = ByteBuffer.allocateDirect(length);
				gl.glGetProgramInfoLog(glObjectIdx, infoLog.limit(), null, infoLog);
				final byte[] infoBytes = new byte[length];
				infoLog.get(infoBytes);
				out = new String(infoBytes);
			}
			logger.error("Shader Program failed to link, " + out);
			isLinked = false;
		} else {
			resolveShaderAttributes(gl);
			resolveUniforms(gl);
			resolveDynamicUniformBlocks(gl);
			isLinked = true;
		}
		return isLinked;
	}

	private void resolveShaderAttributes(GL3 gl) {
		shaderAttributes.clear();
		int[] result = new int[1];

		gl.glGetProgramiv(glObjectIdx, GL3.GL_ACTIVE_ATTRIBUTES, result, 0);
		int numAttributes = result[0];

		gl.glGetProgramiv(glObjectIdx, GL3.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, result, 0);
		int attributeNameMaxLength = result[0];

		for (int i = 0; i < numAttributes; i++) {
			int[] attributeNameLength = new int[1];
			int[] attributeSize = new int[1];
			int[] attributeType = new int[1];
			byte[] attributeName = new byte[attributeNameMaxLength];
			gl.glGetActiveAttrib(glObjectIdx, i, attributeNameMaxLength, attributeNameLength, 0, attributeSize, 0, attributeType, 0, attributeName, 0);

			String name = new String(attributeName, 0, attributeNameLength[0]);
			if (name.startsWith("gl_"))
				continue;

			int attributeLocation = gl.glGetAttribLocation(glObjectIdx, name);
			Attribute shaderAttribute = new Attribute(name, attributeLocation, attributeType[0], attributeSize[0]);
			shaderAttributes.put(name, shaderAttribute);

		}
	}

	private void resolveUniforms(GL3 gl) {
		shaderUniforms.clear();
		int[] result = new int[1];

		gl.glGetProgramiv(glObjectIdx, GL3.GL_ACTIVE_UNIFORMS, result, 0);
		int numUniforms = result[0];

		gl.glGetProgramiv(glObjectIdx, GL3.GL_ACTIVE_UNIFORM_MAX_LENGTH, result, 0);
		int uniformNameMaxLength = result[0];

		for (int i = 0; i < numUniforms; i++) {
			int[] uniformNameLength = new int[1];
			int[] uniformSize = new int[1];
			int[] uniformType = new int[1];
			byte[] uniformName = new byte[uniformNameMaxLength];
			gl.glGetActiveUniform(glObjectIdx, i, uniformNameMaxLength, uniformNameLength, 0, uniformSize, 0, uniformType, 0, uniformName, 0);

			String name = new String(uniformName, 0, uniformNameLength[0]);
			if (name.startsWith("gl_"))
				continue;

			if (name.endsWith("[0]")) { // Correct for ATI issue where array uniforms have [0] suffix
				name = name.substring(0, uniformNameLength[0] - 3);
			}

			int[] uniformIndex = new int[] {i};
			int[] uniformBlockIndex = new int[1];
			gl.glGetActiveUniformsiv(
					glObjectIdx,				// program
					1,							// uniformCount
					uniformIndex, 0,			// uniformIndicies, offset
					GL3.GL_UNIFORM_BLOCK_INDEX, // parameter
					uniformBlockIndex, 0);		// output, offset

			if (uniformBlockIndex[0] != -1)
				continue;

			int uniformLocation = gl.glGetUniformLocation(glObjectIdx, name);
			Uniform uniform = new Uniform(name, uniformLocation, uniformType[0], uniformSize[0]);
			shaderUniforms.put(name, uniform);
		}
	}

	private void resolveDynamicUniformBlocks(GL3 gl) {
		for (String uniformBlockName : DynamicUniformManager.dynamicUniformBlocks.keySet()) {
			linkUniformBlock(gl, uniformBlockName, DynamicUniformManager.dynamicUniformBlocks.get(uniformBlockName));
		}
	}

	public int getAttributeLocation(String attributeName) {
		Attribute attrib = shaderAttributes.get(attributeName);
		if (attrib != null)
			return attrib.location;

		return -1;
	}

	public Attribute getAttribute(String attributeName) {
		return shaderAttributes.get(attributeName);
	}

	public int getFragmentLocation(GL3 gl, String fragmentName) {
		return gl.glGetFragDataLocation(glObjectIdx, fragmentName);
	}

	public Uniform getUniform(String uniformName) {
		return shaderUniforms.get(uniformName);
	}

	public void linkUniformBlock(GL3 gl, String name, UniformBlock uniformBlock) {
		int blockIndex = gl.glGetUniformBlockIndex(glObjectIdx, name);
		if (blockIndex >= 0) {
			gl.glUniformBlockBinding(glObjectIdx, blockIndex, uniformBlock.uniformBlockIndex);
			if (!uniformBlock.isInitialised)
				uniformBlock.initalise(gl);
		}
	}

	public void bind(GL3 gl) {
		if(!isInitialised) {
			logger.error("Attempting to bind unitialised Shader Program");
			return;
		}

		gl.glUseProgram(glObjectIdx);
	}

	public static void unbind(GL3 gl) {
		gl.glUseProgram(0);
	}

	public void clean(GL3 gl, Shape shape) {
		for (Uniform uniform : shaderUniforms.values())
			uniform.clean(gl, shape);
	}

	public void linkupDynamicUniforms() {
		for (Uniform uniform : shaderUniforms.values()) {
			if (!uniform.isInitialised()) {
				DynamicUniform dynamicUniform = DynamicUniformManager.lookupDynamicUniform(uniform.getName());
				if (dynamicUniform != null) {
					uniform.setDynamicUniform(dynamicUniform);
					DynamicUniformManager.addActiveUniform(dynamicUniform);
				}
			}
		}
	}

}
