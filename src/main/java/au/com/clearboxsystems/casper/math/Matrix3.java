package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 6/01/14 2:04 PM
 */
public class Matrix3 {
	//	[ m11, m12, m13;
//	  m21, m22, m23;
//	  m31, m32, m33;]
	public double m11, m12, m13;
	public double m21, m22, m23;
	public double m31, m32, m33;

	public Matrix3() {
	}

	public Matrix3(double m11, double m12, double m13,
	               double m21, double m22, double m23,
	               double m31, double m32, double m33) {
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	public Matrix3(double[] m) {
		this.m11 = m[0];
		this.m12 = m[1];
		this.m13 = m[2];
		this.m21 = m[3];
		this.m22 = m[4];
		this.m23 = m[5];
		this.m31 = m[6];
		this.m32 = m[7];
		this.m33 = m[8];
	}

	public Matrix3(Matrix3 m) {
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
		m31 = m.m31;
		m32 = m.m32;
		m33 = m.m33;
	}

	public static Matrix3 identity() {
		return new Matrix3(1, 0, 0, 0, 1, 0, 0, 0, 1);
	}

	public Matrix3 set(Matrix3 m) {
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
		m31 = m.m31;
		m32 = m.m32;
		m33 = m.m33;
		return this;
	}


	/**
	 * Set the value of this Matrix as a Discrete Cosine Matrix representing
	 * the rotation specified by the euler angles in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * @param euler vector of euler angles in rad [phi (roll), theta (pitch), psi (yaw)]
	 * @return Discrete Cosine Matrix
	 */
	public Matrix3 setEulerDCM(Vector3 euler) {
		return setEulerDCM(euler.x, euler.y, euler.z);
	}

	/**
	 * Set the value of this Matrix as a Discrete Cosine Matrix representing
	 * the rotation specified by the euler angles in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * @param phi roll in rad
	 * @param theta pitch in rad
	 * @param psi yaw in rad
	 * @return Discrete Cosine Matrix
	 */
	public Matrix3 setEulerDCM(double phi, double theta, double psi) {
		final double cphi = Math.cos(phi);
		final double ctheta = Math.cos(theta);
		final double cpsi = Math.cos(psi);

		final double sphi = Math.sin(phi);
		final double stheta = Math.sin(theta);
		final double spsi = Math.sin(psi);

		m11 = cpsi * ctheta;
		m12 = spsi * ctheta;
		m13 = -stheta;
		m21 = -spsi * cphi + cpsi * stheta * sphi;
		m22 = cpsi * cphi + spsi * stheta * sphi;
		m23 = ctheta * sphi;
		m31 = spsi * sphi + cpsi * stheta * cphi;
		m32 = -cpsi * sphi + spsi * stheta * cphi;
		m33 = ctheta * cphi;
		return this;
	}

	/**
	 * Creates a new Discrete Cosine Matrix representing
	 * the rotation specified by the euler angles in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * @param euler vector of euler angles in rad [phi (roll), theta (pitch), psi (yaw)]
	 * @return Discrete Cosine Matrix
	 */
	public static Matrix3 fromEuler(Vector3 euler) {
		return new Matrix3().setEulerDCM(euler.x, euler.y, euler.z);
	}

	/**
	 * Creates a new Discrete Cosine Matrix representing
	 * the rotation specified by the euler angles in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * @param phi roll in rad
	 * @param theta pitch in rad
	 * @param psi yaw in rad
	 * @return Discrete Cosine Matrix
	 */
	public static Matrix3 fromEuler(double phi, double theta, double psi) {
		return new Matrix3().setEulerDCM(phi, theta, psi);
	}

	/**
	 * Generate the euler angle equivalents of this Discrete Cosine Matrix,
	 * The rotation specified by the euler angles are in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * The output of this function is undefined if the underlying matrix is
	 * not a DCM
	 *
	 * @return euler vector of euler angles in rad [phi (roll), theta (pitch), psi (yaw)]
	 */
	public Vector3 getEuler() {
		return new Vector3(
				Math.atan2(m23, m33),
				-Math.asin(m13),
				Math.atan2(m12, m11));
	}

	public Matrix3 scale(double s) {
		m11 *= s;
		m12 *= s;
		m13 *= s;
		m21 *= s;
		m22 *= s;
		m23 *= s;
		m31 *= s;
		m32 *= s;
		m33 *= s;
		return this;
	}

	public Matrix3 nScale(double s) {
		return new Matrix3(
				m11 * s, m12 * s, m13 * s,
				m21 * s, m22 * s, m23 * s,
				m31 * s, m32 * s, m33 * s);
	}

	public Matrix3 transpose() {
		double tmp;
		tmp = m12;
		m12 = m21;
		m21 = tmp;

		tmp = m13;
		m13 = m31;
		m31 = tmp;

		tmp = m32;
		m32 = m23;
		m23 = tmp;
		return this;
	}

	public Matrix3 nTranspose() {
		return new Matrix3(
				m11, m21, m31,
				m12, m22, m32,
				m13, m23, m33);
	}

	public Vector3 times(Vector3 v) {
		double x = m11 * v.x + m12 * v.y + m13 * v.z;
		double y = m21 * v.x + m22 * v.y + m23 * v.z;
		double z = m31 * v.x + m32 * v.y + m33 * v.z;
		v.x = x;
		v.y = y;
		v.z = z;
		return v;
	}

	public Vector3 nTimes(Vector3 v) {
		return new Vector3(
				m11 * v.x + m12 * v.y + m13 * v.z,
				m21 * v.x + m22 * v.y + m23 * v.z,
				m31 * v.x + m32 * v.y + m33 * v.z);
	}

	public Matrix3 nTimes(Matrix3 m) {
		return new Matrix3(
				m11 * m.m11 + m12 * m.m21 + m13 * m.m31,
				m11 * m.m12 + m12 * m.m22 + m13 * m.m32,
				m11 * m.m13 + m12 * m.m23 + m13 * m.m33,
				m21 * m.m11 + m22 * m.m21 + m23 * m.m31,
				m21 * m.m12 + m22 * m.m22 + m23 * m.m32,
				m21 * m.m13 + m22 * m.m23 + m23 * m.m33,
				m31 * m.m11 + m32 * m.m21 + m33 * m.m31,
				m31 * m.m12 + m32 * m.m22 + m33 * m.m32,
				m31 * m.m13 + m32 * m.m23 + m33 * m.m33);
	}

	public Matrix3 times(Matrix3 m) {
		double n11 = m11 * m.m11 + m12 * m.m21 + m13 * m.m31;
		double n12 = m11 * m.m12 + m12 * m.m22 + m13 * m.m32;
		double n13 = m11 * m.m13 + m12 * m.m23 + m13 * m.m33;
		double n21 = m21 * m.m11 + m22 * m.m21 + m23 * m.m31;
		double n22 = m21 * m.m12 + m22 * m.m22 + m23 * m.m32;
		double n23 = m21 * m.m13 + m22 * m.m23 + m23 * m.m33;
		double n31 = m31 * m.m11 + m32 * m.m21 + m33 * m.m31;
		double n32 = m31 * m.m12 + m32 * m.m22 + m33 * m.m32;
		double n33 = m31 * m.m13 + m32 * m.m23 + m33 * m.m33;
		m11 = n11;
		m12 = n12;
		m13 = n13;
		m21 = n21;
		m22 = n22;
		m23 = n23;
		m31 = n31;
		m32 = n32;
		m33 = n33;
		return this;
	}

	public double det() {
		return m11 * (m22 * m33 - m23 * m32)
				- m12 * (m21 * m33 - m23 * m31)
				+ m13 * (m21 * m32 - m22 * m31);
	}

	public Matrix3 inv() {
		double invDet = 1. / (m11 * (m22 * m33 - m23 * m32)
				- m12 * (m21 * m33 - m23 * m31)
				+ m13 * (m21 * m32 - m22 * m31));

		double n11 = invDet * (m33 * m22 - m32 * m23);
		double n12 = -invDet * (m33 * m12 - m32 * m13);
		double n13 = invDet * (m23 * m12 - m22 * m13);
		double n21 = -invDet * (m33 * m21 - m31 * m23);
		double n22 = invDet * (m33 * m11 - m31 * m13);
		double n23 = -invDet * (m23 * m11 - m21 * m13);
		double n31 = invDet * (m32 * m21 - m31 * m22);
		double n32 = -invDet * (m32 * m11 - m31 * m12);
		double n33 = invDet * (m22 * m11 - m21 * m12);
		m11 = n11;
		m12 = n12;
		m13 = n13;
		m21 = n21;
		m22 = n22;
		m23 = n23;
		m31 = n31;
		m32 = n32;
		m33 = n33;
		return this;
	}

	public Matrix3 nInv() {
		double invDet = 1. / (m11 * (m22 * m33 - m23 * m32)
				- m12 * (m21 * m33 - m23 * m31)
				+ m13 * (m21 * m32 - m22 * m31));

		return new Matrix3(
				invDet * (m33 * m22 - m32 * m23),
				-invDet * (m33 * m12 - m32 * m13),
				invDet * (m23 * m12 - m22 * m13),
				-invDet * (m33 * m21 - m31 * m23),
				invDet * (m33 * m11 - m31 * m13),
				-invDet * (m23 * m11 - m21 * m13),
				invDet * (m32 * m21 - m31 * m22),
				-invDet * (m32 * m11 - m31 * m12),
				invDet * (m22 * m11 - m21 * m12));
	}

	@Override
	public String toString() {
		return "[" + m11 + ", " + m12 + ", " + m13 + "; "
				+ m21 + ", " + m22 + ", " + m23 + "; "
				+ m31 + ", " + m32 + ", " + m33 + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matrix3) {
			Matrix3 m = (Matrix3)obj;
			return m11 == m.m11 && m12 == m.m12 && m13 == m.m13
					&& m21 == m.m21 && m22 == m.m22 && m23 == m.m23
					&& m31 == m.m31 && m32 == m.m32 && m33 == m.m33;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m11) ^ (Double.doubleToLongBits(this.m11) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m12) ^ (Double.doubleToLongBits(this.m12) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m13) ^ (Double.doubleToLongBits(this.m13) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m21) ^ (Double.doubleToLongBits(this.m21) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m22) ^ (Double.doubleToLongBits(this.m22) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m23) ^ (Double.doubleToLongBits(this.m23) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m31) ^ (Double.doubleToLongBits(this.m31) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m32) ^ (Double.doubleToLongBits(this.m32) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.m33) ^ (Double.doubleToLongBits(this.m33) >>> 32));
		return hash;
	}

	public static Matrix3 xAxisRotationMatrix(double theta) {
		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);
		return new Matrix3(
				1,	0,			0,
				0,	cTheta,		sTheta,
				0,	-sTheta,	cTheta);
	}

	public Matrix3 setXAxisRotation(double theta) {
		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);

		m11 = 1;		m12 = 0;		m13 = 0;
		m21 = 0;		m22 = cTheta;	m23 = sTheta;
		m31 = 0;		m32 = -sTheta;	m33 = cTheta;
		return this;
	}

	public static Matrix3 yAxisRotationMatrix(double theta) {
		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);
		return new Matrix3(
				cTheta,		0,		-sTheta,
				0,			1,		0,
				sTheta,		0,		cTheta);
	}

	public Matrix3 setYAxisRotation(double theta) {
		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);

		m11 = cTheta;	m12 = 0;		m13 = -sTheta;
		m21 = 0;		m22 = 1;		m23 = 0;
		m31 = sTheta;	m32 = 0;		m33 = cTheta;
		return this;
	}

	public static Matrix3 zAxisRotationMatrix(double theta) {
		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);
		return new Matrix3(
				cTheta,		sTheta,		0,
				-sTheta,	cTheta,		0,
				0,			0,			1);
	}

	public Matrix3 setZAxisRotation(double theta) {
		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);

		m11 = cTheta;	m12 = sTheta;	m13 = 0;
		m21 = -sTheta;	m22 = cTheta;	m23 = 0;
		m31 = 0;		m32 = 0;		m33 = 1;
		return this;
	}
}
