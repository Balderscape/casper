package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 6/01/14 2:05 PM
 */
public class Quaternion {
	/**
	 * Scalar Component (w)
	 */
	public double q0;	// Scalar Component
	/**
	 * x component (i)
	 */
	public double q1;	// i (x)
	/**
	 * y component (j)
	 */
	public double q2;	// j (y)
	/**
	 * z component (k)
	 */
	public double q3;	// k (z)

	public Quaternion() {
		q0 = 1;
		q1 = 0;
		q2 = 0;
		q3 = 0;
	}

	public Quaternion(double angle, Vector3 axis) {
		final double sinAngle = Math.sin(0.5 * angle);

		q0 = Math.cos(0.5 * angle);
		q1 = axis.x * sinAngle;
		q2 = axis.y * sinAngle;
		q3 = axis.z * sinAngle;
	}

	public Quaternion(double q0, double q1, double q2, double q3) {
		this.q0 = q0;
		this.q1 = q1;
		this.q2 = q2;
		this.q3 = q3;
	}

	public Quaternion(Quaternion q) {
		q0 = q.q0;
		q1 = q.q1;
		q2 = q.q2;
		q3 = q.q3;
	}

	public void set(Quaternion q) {
		q0 = q.q0;
		q1 = q.q1;
		q2 = q.q2;
		q3 = q.q3;
	}

	public void set(double q0, double q1, double q2, double q3) {
		this.q0 = q0;
		this.q1 = q1;
		this.q2 = q2;
		this.q3 = q3;
	}

	/**
	 * Set quaternion to rotation defined by euler angles in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * @param euler - vector of euler angles in rad [phi (roll), theta (pitch), psi (yaw)]
	 * @return
	 */
	public Quaternion setEuler(Vector3 euler) {
		return setEuler(euler.x, euler.y, euler.z);
	}

	/**
	 * Set quaternion to rotation defined by euler angles in aerospace sequence
	 * yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 7.6, pp 166-167
	 *
	 * @param phi roll (rad)
	 * @param theta pitch (rad)
	 * @param psi yaw (rad)
	 * @return
	 */
	public Quaternion setEuler(double phi, double theta, double psi) {
		double hPhi = phi * 0.5;
		double hTheta = theta * 0.5;
		double hPsi = psi * 0.5;
		double cPhi = Math.cos(hPhi);
		double cTheta = Math.cos(hTheta);
		double cPsi = Math.cos(hPsi);
		double sPhi = Math.sin(hPhi);
		double sTheta = Math.sin(hTheta);
		double sPsi = Math.sin(hPsi);

		q0 = cPhi * cTheta * cPsi + sPhi * sTheta * sPsi;
		q1 = sPhi * cTheta * cPsi - cPhi * sTheta * sPsi;
		q2 = cPhi * sTheta * cPsi + sPhi * cTheta * sPsi;
		q3 = cPhi * cTheta * sPsi - sPhi * sTheta * cPsi;
		return this;
	}

	public static Quaternion fromEuler(Vector3 euler) {
		return new Quaternion().setEuler(euler.x, euler.y, euler.z);
	}

	public static Quaternion fromEuler(double phi, double theta, double psi) {
		return new Quaternion().setEuler(phi, theta, psi);
	}

	/**
	 * Returns the euler angle representation of the rotation defined by this
	 * quaternion in aerospace sequence yaw (psi/z), pitch (theta/y), roll (phi/x)
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 7.8, pg 168
	 *
	 * @return vector of euler angles in rad [phi (roll), theta (pitch), psi (yaw)]
	 */
	public Vector3 getEuler() {
		double phi = Math.atan2(
				2 * (q2 * q3 + q0 * q1),
				2 * (q0 * q0 + q3 * q3) - 1);

		double theta = -Math.asin(
				2 * (q1 * q3 - q0 * q2));

		double psi = Math.atan2(
				2 * (q1 * q2 + q0 * q3),
				2 * (q0 * q0 + q1 * q1) - 1);

		return new Vector3(phi, theta, psi);
	}

	/**
	 * Set quaternion to rotation defined by a Discrete Cosine Matrix
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 7.9, pp 168-169
	 *
	 * @param m DCM
	 * @return
	 */
	public Quaternion setDCM(Matrix3 m) {
		final double t = 1. + m.m11 + m.m22 + m.m33;
		if (t > 0.000000000000001) {
			q0 = Math.sqrt(t) * 0.5;
			double s = 1. / (4 * q0);
			q1 = s * (m.m23 - m.m32);
			q2 = s * (m.m31 - m.m13);
			q3 = s * (m.m12 - m.m21);
		} else if ((m.m11 > m.m22) && (m.m11 > m.m33)) {
			q1 = Math.sqrt(1.0 + m.m11 - m.m22 - m.m33) * 0.5;
			double s = 1. / (4 * q1);
			q2 = s * (m.m12 + m.m21);
			q3 = s * (m.m31 + m.m13);
			q0 = s * (m.m23 - m.m32);
		} else if (m.m22 > m.m33) {
			q2 = Math.sqrt(1.0 + m.m22 - m.m11 - m.m33) * 0.5;
			double s = 1. / (4 * q2);
			q1 = s * (m.m12 + m.m21);
			q3 = s * (m.m23 + m.m32);
			q0 = s * (m.m31 - m.m13);
		} else {
			q3 = Math.sqrt(1.0 + m.m33 - m.m11 - m.m22) * 0.5;
			double s = 1. / (4 * q2);
			q1 = s * (m.m31 + m.m13);
			q2 = s * (m.m23 + m.m32);
			q0 = s * (m.m12 - m.m21);
		}
		return this;
	}

	/**
	 * Create a Discrete Cosine Matrix representation of the rotation
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 7.7, pp 167-168
	 *
	 * @return DCM
	 */
	public Matrix3 getDCM() {
		Matrix3 dcm = new Matrix3();
		dcm.m11 = 1.0 - 2 * (q2 * q2 + q3 * q3);
		dcm.m12 = 2 * (q1 * q2 + q0 * q3);
		dcm.m13 = 2 * (q1 * q3 - q0 * q2);
		dcm.m21 = 2 * (q1 * q2 - q0 * q3);
		dcm.m22 = 1.0 - 2 * (q1 * q1 + q3 * q3);
		dcm.m23 = 2 * (q2 * q3 + q0 * q1);
		dcm.m31 = 2 * (q1 * q3 + q0 * q2);
		dcm.m32 = 2 * (q2 * q3 - q0 * q1);
		dcm.m33 = 1.0 - 2 * (q1 * q1 + q2 * q2);
		return dcm;
	}

	/**
	 * Creates a new quaternion whose rotation is defined by a Discrete Cosine Matrix
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 7.9, pp 168-169
	 *
	 * @param m DCM
	 * @return
	 */
	public static Quaternion fromDCM(Matrix3 m) {
		Quaternion q = new Quaternion();
		return q.setDCM(m);
	}

	public Quaternion add(Quaternion q) {
		q0 += q.q0;
		q1 += q.q1;
		q2 += q.q2;
		q3 += q.q3;
		return this;
	}

	public Quaternion nAdd(Quaternion q) {
		return new Quaternion(q0 + q.q0, q1 + q.q1, q2 + q.q2, q3 + q.q3);
	}

	public Quaternion times(double s) {
		q0 *= s;
		q1 *= s;
		q2 *= s;
		q3 *= s;
		return this;
	}

	public Quaternion nTimes(double s) {
		return new Quaternion(q0 * s, q1 * s, q2 * s, q3 * s);
	}

	/**
	 * Quaternion multiplication - uses original quaternion to store the result
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 5.4, eq 5.3 pg 109
	 * @param q
	 * @return
	 */
	public Quaternion times(Quaternion q) {
		double n0 = q0 * q.q0 - q1 * q.q1 - q2 * q.q2 - q3 * q.q3;
		double n1 = q1 * q.q0 + q0 * q.q1 - q3 * q.q2 + q2 * q.q3;
		double n2 = q2 * q.q0 + q3 * q.q1 + q0 * q.q2 - q1 * q.q3;
		double n3 = q3 * q.q0 - q2 * q.q1 + q1 * q.q2 + q0 * q.q3;
		q0 = n0;
		q1 = n1;
		q2 = n2;
		q3 = n3;
		return this;
	}

	/**
	 * Quaternion multiplication - creates a new quaternion for the result
	 *
	 * ref - Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *		- Sec 5.4, eq 5.3 pg 109
	 * @param q
	 * @return
	 */
	public Quaternion nTimes(Quaternion q) {
		return new Quaternion(
				q0 * q.q0 - q1 * q.q1 - q2 * q.q2 - q3 * q.q3,
				q1 * q.q0 + q0 * q.q1 - q3 * q.q2 + q2 * q.q3,
				q2 * q.q0 + q3 * q.q1 + q0 * q.q2 - q1 * q.q3,
				q3 * q.q0 - q2 * q.q1 + q1 * q.q2 + q0 * q.q3);
	}

	/**
	 * Rotates a vector by the quaternion
	 *
	 * ref	- Quaternions & Rotation Sequences 5th print 2002 Jack B. Quipers
	 *			- Sec 5.4, eq 5.6 pg 133
	 *		- Aircraft simulation and control 1st ed, 2003, B.L. Stevens et al
	 *			- Sec 1.2, eq 1.2-20b pg 19
	 *
	 * note: this is algebraically equivalent to multiplying the vector by the DCM
	 * @param v input vector
	 * @return rotated vector
	 */
	public Vector3 rotate(Vector3 v) {
		Quaternion qv = new Quaternion(0, v.x, v.y, v.z);
		qv.times(this);
		qv = nConj().times(qv);
		return new Vector3(qv.q1, qv.q2, qv.q3);
	}

	public Quaternion conj() {
		q1 = -q1;
		q2 = -q2;
		q3 = -q3;
		return this;
	}

	public Quaternion nConj() {
		return new Quaternion(q0, -q1, -q2, -q3);
	}

	/**
	 * Quaternion derivative based on body rates
	 *
	 *		- Aircraft simulation and control 1st ed, 2003, B.L. Stevens et al
	 *			- Sec 1.3, eq 1.3-36 pp 33-34
	 *
	 * @param pqr vector of angular rates of the rotating body in rad
	 * [phi (roll), theta(pitch), psi(yaw)]
	 * @return derivative of the quaternion
	 */
	public Quaternion nDeriv(Vector3 pqr) {
		final double p = pqr.x * 0.5;
		final double q = pqr.y * 0.5;
		final double r = pqr.z * 0.5;

		return new Quaternion(
				-p * q1 - q * q2 - r * q3,
				p * q0			 + r * q2 - q * q3,
				q * q0  - r * q1		  + p * q3,
				r * q0  + q * q1 - p * q2);
	}

	public void norm() {
		double invMag = 1. / Math.sqrt(q0 * q0 + q1 * q1 + q2 * q2 + q3 * q3);
		q0 *= invMag;
		q1 *= invMag;
		q2 *= invMag;
		q3 *= invMag;
	}

	@Override
	public String toString() {
		return "[" + q0 + " , " + q1 + " , " + q2 + " , " + q3 + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Quaternion) {
			Quaternion q = (Quaternion)obj;
			return (q0 == q.q0 && q1 == q.q1 && q2 == q.q2 && q3 == q.q3)
					|| (q0 == -q.q0 && q1 == -q.q1 && q2 == -q.q2 && q3 == -q.q3);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		boolean flip = q0 < 0;
		double t0 = flip ? -q0 : q0;
		double t1 = flip ? -q1 : q1;
		double t2 = flip ? -q2 : q2;
		double t3 = flip ? -q3 : q3;

		hash = 83 * hash + (int) (Double.doubleToLongBits(t0) ^ (Double.doubleToLongBits(t0) >>> 32));
		hash = 83 * hash + (int) (Double.doubleToLongBits(t1) ^ (Double.doubleToLongBits(t1) >>> 32));
		hash = 83 * hash + (int) (Double.doubleToLongBits(t2) ^ (Double.doubleToLongBits(t2) >>> 32));
		hash = 83 * hash + (int) (Double.doubleToLongBits(t3) ^ (Double.doubleToLongBits(t3) >>> 32));
		return hash;
	}
}
