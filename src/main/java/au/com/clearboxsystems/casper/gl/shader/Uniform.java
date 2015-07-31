package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shape.Shape;
import au.com.clearboxsystems.casper.math.*;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * User: pauls
 * Timestamp: 6/01/14 1:59 PM
 */
public class Uniform {

	private static final Logger logger = LoggerFactory.getLogger(Uniform.class);

	public final String name;
	public final int location;
	public final UniformType type;
	public final int size;

	private UniformState state = UniformState.Uninitialised;
	private int[] ibuf;
	private float[] fbuf;
	private double[] dbuf;

	private boolean hasWarned = false;
	private DynamicUniform dynamicUniform;

	public enum UniformState {
		Uninitialised,
		Dirty,
		Clean;
	}

	public Uniform(String name, int location, int gltype, int size) {
		this.name = name;
		this.location = location;
		this.type = UniformType.fromGLType(gltype);
		this.size = size;
	}

	public boolean isInitialised() {
		return state != UniformState.Uninitialised;
	}

	public void setDynamicUniform(DynamicUniform uniform) {
		dynamicUniform = uniform;
	}

	public String getName() {
		return name;
	}

	public enum UniformType {
		Int(GL3.GL_INT),
		UnsignedInt(GL3.GL_UNSIGNED_INT),
		Float(GL3.GL_FLOAT),
		FloatVec2(GL3.GL_FLOAT_VEC2),
		FloatVec3(GL3.GL_FLOAT_VEC3),
		FloatVec4(GL3.GL_FLOAT_VEC4),
		IntVec2(GL3.GL_INT_VEC2),
		IntVec3(GL3.GL_INT_VEC3),
		IntVec4(GL3.GL_INT_VEC4),
		Bool(GL3.GL_BOOL),
		BoolVec2(GL3.GL_BOOL_VEC2),
		BoolVec3(GL3.GL_BOOL_VEC3),
		BoolVec4(GL3.GL_BOOL_VEC4),
		FloatMat2(GL3.GL_FLOAT_MAT2),
		FloatMat3(GL3.GL_FLOAT_MAT3),
		FloatMat4(GL3.GL_FLOAT_MAT4),
		Sampler1D(GL3.GL_SAMPLER_1D),
		Sampler2D(GL3.GL_SAMPLER_2D),
		Sampler3D(GL3.GL_SAMPLER_3D),
		SamplerCube(GL3.GL_SAMPLER_CUBE),
		Sampler1DShadow(GL3.GL_SAMPLER_1D_SHADOW),
		Sampler2DShadow(GL3.GL_SAMPLER_2D_SHADOW),
		Sampler2DRect(GL3.GL_SAMPLER_2D_RECT),
		Sampler2DRectShadow(GL3.GL_SAMPLER_2D_RECT_SHADOW),
		FloatMat2x3(GL3.GL_FLOAT_MAT2x3),
		FloatMat2x4(GL3.GL_FLOAT_MAT2x4),
		FloatMat3x2(GL3.GL_FLOAT_MAT3x2),
		FloatMat3x4(GL3.GL_FLOAT_MAT3x4),
		FloatMat4x2(GL3.GL_FLOAT_MAT4x2),
		FloatMat4x3(GL3.GL_FLOAT_MAT4x3),
		Sampler1DArray(GL3.GL_SAMPLER_1D_ARRAY),
		Sampler2DArray(GL3.GL_SAMPLER_2D_ARRAY),
		SamplerBuffer(GL3.GL_SAMPLER_BUFFER),
		Sampler1DArrayShadow(GL3.GL_SAMPLER_1D_ARRAY_SHADOW),
		Sampler2DArrayShadow(GL3.GL_SAMPLER_2D_ARRAY_SHADOW),
		SamplerCubeShadow(GL3.GL_SAMPLER_CUBE_SHADOW),
		UnsignedIntVec2(GL3.GL_UNSIGNED_INT_VEC2),
		UnsignedIntVec3(GL3.GL_UNSIGNED_INT_VEC3),
		UnsignedIntVec4(GL3.GL_UNSIGNED_INT_VEC4),
		IntSampler1D(GL3.GL_INT_SAMPLER_1D),
		IntSampler2D(GL3.GL_INT_SAMPLER_2D),
		IntSampler3D(GL3.GL_INT_SAMPLER_3D),
		IntSamplerCube(GL3.GL_INT_SAMPLER_CUBE),
		IntSampler2DRect(GL3.GL_INT_SAMPLER_2D_RECT),
		IntSampler1DArray(GL3.GL_INT_SAMPLER_1D_ARRAY),
		IntSampler2DArray(GL3.GL_INT_SAMPLER_2D_ARRAY),
		IntSamplerBuffer(GL3.GL_INT_SAMPLER_BUFFER),
		UnsignedIntSampler1D(GL3.GL_UNSIGNED_INT_SAMPLER_1D),
		UnsignedIntSampler2D(GL3.GL_UNSIGNED_INT_SAMPLER_2D),
		UnsignedIntSampler3D(GL3.GL_UNSIGNED_INT_SAMPLER_3D),
		UnsignedIntSamplerCube(GL3.GL_UNSIGNED_INT_SAMPLER_CUBE),
		UnsignedIntSampler2DRect(GL3.GL_UNSIGNED_INT_SAMPLER_2D_RECT),
		UnsignedIntSampler1DArray(GL3.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY),
		UnsignedIntSampler2DArray(GL3.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY),
		UnsignedIntSamplerBuffer(GL3.GL_UNSIGNED_INT_SAMPLER_BUFFER),
		Sampler2DMultisample(GL3.GL_SAMPLER_2D_MULTISAMPLE),
		IntSampler2DMultisample(GL3.GL_INT_SAMPLER_2D_MULTISAMPLE),
		UnsignedIntSampler2DMultisample(GL3.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE),
		Sampler2DMultisampleArray(GL3.GL_SAMPLER_2D_MULTISAMPLE_ARRAY),
		IntSampler2DMultisampleArray(GL3.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY),
		UnsignedIntSampler2DMultisampleArray(GL3.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY);

		public int glType;

		private UniformType(int glType) {
			this.glType = glType;
		}

		public static UniformType fromGLType(int glType) {
			for (UniformType t : values())
				if (t.glType == glType)
					return t;
			return null;
		}

	}

	public void setValue(boolean val) {
		if (type != UniformType.Bool)
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a int value");

		if (ibuf == null) {
			ibuf = new int[1];
		}
		ibuf[0] = val ? 1 : 0;
		state = UniformState.Dirty;
	}

	public void setValue(int val) {
		switch (type) {
			case Bool:
			case Int:
			case UnsignedInt:
			case Sampler1D:
			case Sampler1DArray:
			case Sampler1DArrayShadow:
			case Sampler1DShadow:
			case Sampler2D:
			case Sampler2DArray:
			case Sampler2DArrayShadow:
			case Sampler2DMultisample:
			case Sampler2DMultisampleArray:
			case Sampler2DRect:
			case Sampler2DRectShadow:
			case Sampler2DShadow:
			case Sampler3D:
			case SamplerBuffer:
			case SamplerCube:
			case SamplerCubeShadow:
			case IntSampler1D:
			case IntSampler1DArray:
			case IntSampler2D:
			case IntSampler2DArray:
			case IntSampler2DMultisample:
			case IntSampler2DMultisampleArray:
			case IntSampler2DRect:
			case IntSampler3D:
			case IntSamplerBuffer:
			case IntSamplerCube:
			case UnsignedIntSampler1D:
			case UnsignedIntSampler1DArray:
			case UnsignedIntSampler2D:
			case UnsignedIntSampler2DArray:
			case UnsignedIntSampler2DMultisample:
			case UnsignedIntSampler2DMultisampleArray:
			case UnsignedIntSampler2DRect:
			case UnsignedIntSampler3D:
			case UnsignedIntSamplerBuffer:
			case UnsignedIntSamplerCube:
				if (ibuf == null) {
					ibuf = new int[1];
				}
				ibuf[0] = val;
				break;
			case Float:
				if (fbuf == null) {
					fbuf = new float[1];
				}
				fbuf[0] = val;
				break;
			default:
				throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a int value");
		}
		state = UniformState.Dirty;
	}

	public void setValue(Vector2 val) {
		if (type != UniformType.FloatVec2) {
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a Vector2 value");
		}

		if (fbuf == null) {
			fbuf = new float[2];
		}

		fbuf[0] = (float)val.x;
		fbuf[1] = (float)val.y;
		state = UniformState.Dirty;
	}

	public void setValue(Vector3 val) {
		if (type != UniformType.FloatVec3) {
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a Vector3 value");
		}

		if (fbuf == null) {
			fbuf = new float[3];
		}

		fbuf[0] = (float)val.x;
		fbuf[1] = (float)val.y;
		fbuf[2] = (float)val.z;
		state = UniformState.Dirty;
	}

	public void setValue(Vector4 val) {
		if (type != UniformType.FloatVec4) {
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a Vector4 value");
		}

		if (fbuf == null) {
			fbuf = new float[4];
		}

		fbuf[0] = (float)val.x;
		fbuf[1] = (float)val.y;
		fbuf[2] = (float)val.z;
		fbuf[3] = (float)val.w;
		state = UniformState.Dirty;
	}

	public void setValue(double val) {
		setValue((float) val);
	}

	public void setValue(float val) {
		if (type != UniformType.Float) {
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a float value");
		}

		if (fbuf == null) {
			fbuf = new float[1];
		}

		fbuf[0] = val;
		state = UniformState.Dirty;
	}

	public void setValue(Color val) {
		if (type != UniformType.FloatVec4 && type != UniformType.FloatVec3) {
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a color value");
		}

		if (fbuf == null) {
			fbuf = new float[4];
		}

		fbuf[0] = (float) (val.getRed() / 255.);
		fbuf[1] = (float) (val.getGreen() / 255.);
		fbuf[2] = (float) (val.getBlue() / 255.);
		fbuf[3] = (float) (val.getAlpha() / 255.);
		state = UniformState.Dirty;
	}

	public void setValue(Matrix4 val) {
		if (type != UniformType.FloatMat4) {
			throw new UnsupportedOperationException("ERROR Cannot set " + type + " to a float mat4 value");
		}

		if (fbuf == null) {
			fbuf = new float[16];
		}

		fbuf[0] = (float) val.m11;
		fbuf[1] = (float) val.m21;
		fbuf[2] = (float) val.m31;
		fbuf[3] = (float) val.m41;
		fbuf[4] = (float) val.m12;
		fbuf[5] = (float) val.m22;
		fbuf[6] = (float) val.m32;
		fbuf[7] = (float) val.m42;
		fbuf[8] = (float) val.m13;
		fbuf[9] = (float) val.m23;
		fbuf[10] = (float) val.m33;
		fbuf[11] = (float) val.m43;
		fbuf[12] = (float) val.m14;
		fbuf[13] = (float) val.m24;
		fbuf[14] = (float) val.m34;
		fbuf[15] = (float) val.m44;
		state = UniformState.Dirty;
	}

	public void clean(GL3 gl, Shape shape) {
		if (dynamicUniform != null) {
			dynamicUniform.setUniform(shape, this);
		}

		if (state == UniformState.Clean)
			return;

		if (state == UniformState.Uninitialised) {
			if (!hasWarned) {
				logger.warn("Uniform " + name + " has not been initialised");
				hasWarned = true;
			}
			return;
		}

		switch (type) {
			case Bool:
			case Int:
			case UnsignedInt:
			case Sampler1D:
			case Sampler1DArray:
			case Sampler1DArrayShadow:
			case Sampler1DShadow:
			case Sampler2D:
			case Sampler2DArray:
			case Sampler2DArrayShadow:
			case Sampler2DMultisample:
			case Sampler2DMultisampleArray:
			case Sampler2DRect:
			case Sampler2DRectShadow:
			case Sampler2DShadow:
			case Sampler3D:
			case SamplerBuffer:
			case SamplerCube:
			case SamplerCubeShadow:
			case IntSampler1D:
			case IntSampler1DArray:
			case IntSampler2D:
			case IntSampler2DArray:
			case IntSampler2DMultisample:
			case IntSampler2DMultisampleArray:
			case IntSampler2DRect:
			case IntSampler3D:
			case IntSamplerBuffer:
			case IntSamplerCube:
			case UnsignedIntSampler1D:
			case UnsignedIntSampler1DArray:
			case UnsignedIntSampler2D:
			case UnsignedIntSampler2DArray:
			case UnsignedIntSampler2DMultisample:
			case UnsignedIntSampler2DMultisampleArray:
			case UnsignedIntSampler2DRect:
			case UnsignedIntSampler3D:
			case UnsignedIntSamplerBuffer:
			case UnsignedIntSamplerCube:
				gl.glUniform1iv(location, size, ibuf, 0);
				break;
			case BoolVec2:
			case IntVec2:
			case UnsignedIntVec2:
				gl.glUniform2iv(location, size, ibuf, 0);
				break;
			case BoolVec3:
			case IntVec3:
			case UnsignedIntVec3:
				gl.glUniform3iv(location, size, ibuf, 0);
				break;
			case BoolVec4:
			case IntVec4:
			case UnsignedIntVec4:
				gl.glUniform4iv(location, size, ibuf, 0);
				break;
			case Float:
				gl.glUniform1fv(location, size, fbuf, 0);
				break;
			case FloatVec2:
				gl.glUniform2fv(location, size, fbuf, 0);
				break;
			case FloatVec3:
				gl.glUniform3fv(location, size, fbuf, 0);
				break;
			case FloatVec4:
				gl.glUniform4fv(location, size, fbuf, 0);
				break;
			case FloatMat2:
				gl.glUniformMatrix2fv(location, size, false, fbuf, 0);
				break;
			case FloatMat2x3:
				gl.glUniformMatrix2x3fv(location, size, false, fbuf, 0);
				break;
			case FloatMat2x4:
				gl.glUniformMatrix2x4fv(location, size, false, fbuf, 0);
				break;
			case FloatMat3:
				gl.glUniformMatrix3fv(location, size, false, fbuf, 0);
				break;
			case FloatMat3x2:
				gl.glUniformMatrix3x2fv(location, size, false, fbuf, 0);
				break;
			case FloatMat3x4:
				gl.glUniformMatrix3x4fv(location, size, false, fbuf, 0);
				break;
			case FloatMat4:
				gl.glUniformMatrix4fv(location, size, false, fbuf, 0);
				break;
			case FloatMat4x2:
				gl.glUniformMatrix4x2fv(location, size, false, fbuf, 0);
				break;
			case FloatMat4x3:
				gl.glUniformMatrix4x3fv(location, size, false, fbuf, 0);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported Data Type " + type);
		}
		state = UniformState.Clean;
	}
}
