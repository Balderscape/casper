package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.nio.ByteBuffer;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:04 PM
 */
public class Matrix4 {

	public double m11, m12, m13, m14;
	public double m21, m22, m23, m24;
	public double m31, m32, m33, m34;
	public double m41, m42, m43, m44;

	public Matrix4() {
	}

	public Matrix4(double m11, double m12, double m13, double m14, double m21, double m22, double m23, double m24, double m31, double m32, double m33, double m34, double m41, double m42, double m43, double m44) {
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m14 = m14;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m24 = m24;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
		this.m34 = m34;
		this.m41 = m41;
		this.m42 = m42;
		this.m43 = m43;
		this.m44 = m44;
	}

	public void set(Matrix4 m) {
		this.m11 = m.m11;
		this.m12 = m.m12;
		this.m13 = m.m13;
		this.m14 = m.m14;
		this.m21 = m.m21;
		this.m22 = m.m22;
		this.m23 = m.m23;
		this.m24 = m.m24;
		this.m31 = m.m31;
		this.m32 = m.m32;
		this.m33 = m.m33;
		this.m34 = m.m34;
		this.m41 = m.m41;
		this.m42 = m.m42;
		this.m43 = m.m43;
		this.m44 = m.m44;
	}

	public void toByteBufferAsFloat(ByteBuffer buffer, int offset, boolean rowFirst) {
		if (rowFirst) {
			buffer.putFloat(offset     , (float) m11);
			buffer.putFloat(offset + 4 , (float) m12);
			buffer.putFloat(offset + 8 , (float) m13);
			buffer.putFloat(offset + 12, (float) m14);

			buffer.putFloat(offset + 16, (float) m21);
			buffer.putFloat(offset + 20, (float) m22);
			buffer.putFloat(offset + 24, (float) m23);
			buffer.putFloat(offset + 28, (float) m24);

			buffer.putFloat(offset + 32, (float) m31);
			buffer.putFloat(offset + 36, (float) m32);
			buffer.putFloat(offset + 40, (float) m33);
			buffer.putFloat(offset + 44, (float) m34);

			buffer.putFloat(offset + 48, (float) m41);
			buffer.putFloat(offset + 52, (float) m42);
			buffer.putFloat(offset + 56, (float) m43);
			buffer.putFloat(offset + 60, (float) m44);
		} else {
			buffer.putFloat(offset     , (float) m11);
			buffer.putFloat(offset + 4 , (float) m21);
			buffer.putFloat(offset + 8 , (float) m31);
			buffer.putFloat(offset + 12, (float) m41);

			buffer.putFloat(offset + 16, (float) m12);
			buffer.putFloat(offset + 20, (float) m22);
			buffer.putFloat(offset + 24, (float) m32);
			buffer.putFloat(offset + 28, (float) m42);

			buffer.putFloat(offset + 32, (float) m13);
			buffer.putFloat(offset + 36, (float) m23);
			buffer.putFloat(offset + 40, (float) m33);
			buffer.putFloat(offset + 44, (float) m43);

			buffer.putFloat(offset + 48, (float) m14);
			buffer.putFloat(offset + 52, (float) m24);
			buffer.putFloat(offset + 56, (float) m34);
			buffer.putFloat(offset + 60, (float) m44);
		}
	}

	public static Matrix4 identity() {
		return new Matrix4(1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	public Matrix4 times(Matrix4 m) {
		double n11 = m11 * m.m11 + m12 * m.m21 + m13 * m.m31 + m14 * m.m41;
		double n12 = m11 * m.m12 + m12 * m.m22 + m13 * m.m32 + m14 * m.m42;
		double n13 = m11 * m.m13 + m12 * m.m23 + m13 * m.m33 + m14 * m.m43;
		double n14 = m11 * m.m14 + m12 * m.m24 + m13 * m.m34 + m14 * m.m44;

		double n21 = m21 * m.m11 + m22 * m.m21 + m23 * m.m31 + m24 * m.m41;
		double n22 = m21 * m.m12 + m22 * m.m22 + m23 * m.m32 + m24 * m.m42;
		double n23 = m21 * m.m13 + m22 * m.m23 + m23 * m.m33 + m24 * m.m43;
		double n24 = m21 * m.m14 + m22 * m.m24 + m23 * m.m34 + m24 * m.m44;

		double n31 = m31 * m.m11 + m32 * m.m21 + m33 * m.m31 + m34 * m.m41;
		double n32 = m31 * m.m12 + m32 * m.m22 + m33 * m.m32 + m34 * m.m42;
		double n33 = m31 * m.m13 + m32 * m.m23 + m33 * m.m33 + m34 * m.m43;
		double n34 = m31 * m.m14 + m32 * m.m24 + m33 * m.m34 + m34 * m.m44;

		double n41 = m41 * m.m11 + m42 * m.m21 + m43 * m.m31 + m44 * m.m41;
		double n42 = m41 * m.m12 + m42 * m.m22 + m43 * m.m32 + m44 * m.m42;
		double n43 = m41 * m.m13 + m42 * m.m23 + m43 * m.m33 + m44 * m.m43;
		double n44 = m41 * m.m14 + m42 * m.m24 + m43 * m.m34 + m44 * m.m44;

		m11 = n11;
		m12 = n12;
		m13 = n13;
		m14 = n14;

		m21 = n21;
		m22 = n22;
		m23 = n23;
		m24 = n24;

		m31 = n31;
		m32 = n32;
		m33 = n33;
		m34 = n34;

		m41 = n41;
		m42 = n42;
		m43 = n43;
		m44 = n44;
		return this;
	}

	public Matrix4 nTimes(Matrix4 m) {
		return new Matrix4(
				m11 * m.m11 + m12 * m.m21 + m13 * m.m31 + m14 * m.m41,
				m11 * m.m12 + m12 * m.m22 + m13 * m.m32 + m14 * m.m42,
				m11 * m.m13 + m12 * m.m23 + m13 * m.m33 + m14 * m.m43,
				m11 * m.m14 + m12 * m.m24 + m13 * m.m34 + m14 * m.m44,
				m21 * m.m11 + m22 * m.m21 + m23 * m.m31 + m24 * m.m41,
				m21 * m.m12 + m22 * m.m22 + m23 * m.m32 + m24 * m.m42,
				m21 * m.m13 + m22 * m.m23 + m23 * m.m33 + m24 * m.m43,
				m21 * m.m14 + m22 * m.m24 + m23 * m.m34 + m24 * m.m44,
				m31 * m.m11 + m32 * m.m21 + m33 * m.m31 + m34 * m.m41,
				m31 * m.m12 + m32 * m.m22 + m33 * m.m32 + m34 * m.m42,
				m31 * m.m13 + m32 * m.m23 + m33 * m.m33 + m34 * m.m43,
				m31 * m.m14 + m32 * m.m24 + m33 * m.m34 + m34 * m.m44,
				m41 * m.m11 + m42 * m.m21 + m43 * m.m31 + m44 * m.m41,
				m41 * m.m12 + m42 * m.m22 + m43 * m.m32 + m44 * m.m42,
				m41 * m.m13 + m42 * m.m23 + m43 * m.m33 + m44 * m.m43,
				m41 * m.m14 + m42 * m.m24 + m43 * m.m34 + m44 * m.m44);
	}

	public void setSubMatrix(int row, int col, Matrix3 m) {
		if (row == 0 && col == 0) {
			m11 = m.m11;
			m12 = m.m12;
			m13 = m.m13;
			m21 = m.m21;
			m22 = m.m22;
			m23 = m.m23;
			m31 = m.m31;
			m32 = m.m32;
			m33 = m.m33;
		} else if (row == 1 && col == 0) {
			m21 = m.m11;
			m22 = m.m12;
			m23 = m.m13;
			m31 = m.m21;
			m32 = m.m22;
			m33 = m.m23;
			m41 = m.m31;
			m42 = m.m32;
			m43 = m.m33;
		} else if (row == 0 && col == 1) {
			m12 = m.m11;
			m13 = m.m12;
			m14 = m.m13;
			m22 = m.m21;
			m23 = m.m22;
			m24 = m.m23;
			m32 = m.m31;
			m33 = m.m32;
			m34 = m.m33;
		} else if (row == 1 && col == 1) {
			m22 = m.m11;
			m23 = m.m12;
			m24 = m.m13;
			m32 = m.m21;
			m33 = m.m22;
			m34 = m.m23;
			m42 = m.m31;
			m43 = m.m32;
			m44 = m.m33;
		}
	}
}
