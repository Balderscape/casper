package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import com.jogamp.opengl.GL3;

/**
 * User: pauls
 * Timestamp: 6/01/14 1:58 PM
 */
public class Attribute {
	public final String name;
	public final int location;
	public final ShaderVertexAttributeType type;
	public final int size;

	public Attribute(String name, int location, int glType, int size) {
		this.name = name;
		this.location = location;
		type = ShaderVertexAttributeType.fromGLType(glType);
		this.size = size;
	}

	public enum ShaderVertexAttributeType {
		Float(GL3.GL_FLOAT),
		FloatVector2(GL3.GL_FLOAT_VEC2),
		FloatVector3(GL3.GL_FLOAT_VEC3),
		FloatVector4(GL3.GL_FLOAT_VEC4),
		FloatMatrix22(GL3.GL_FLOAT_MAT2),
		FloatMatrix33(GL3.GL_FLOAT_MAT3),
		FloatMatrix44(GL3.GL_FLOAT_MAT4),
		Int(GL3.GL_INT),
		IntVector2(GL3.GL_INT_VEC2),
		IntVector3(GL3.GL_INT_VEC3),
		IntVector4(GL3.GL_INT_VEC4);

		public final int glType;

		private ShaderVertexAttributeType(int glType) {
			this.glType = glType;
		}

		public static ShaderVertexAttributeType fromGLType(int glType) {
			for (ShaderVertexAttributeType t : values())
				if (t.glType == glType)
					return t;
			return null;
		}
	}

}
