package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.nio.ByteBuffer;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:03 PM
 */
public class Vector3 {
	public double x;
	public double y;
	public double z;

	public Vector3() {
	}

	public Vector3(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(Vector3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void toByteBufferAsFloat(ByteBuffer buffer, int offset) {
		buffer.putFloat(offset, (float)x);
		buffer.putFloat(offset + 4, (float)y);
		buffer.putFloat(offset + 8, (float)z);
	}

	public void swap(Vector3 v) {
		double tmp = x;
		x = v.x;
		v.x = tmp;

		tmp = y;
		y = v.y;
		v.y = tmp;

		tmp = z;
		z = v.z;
		v.z = tmp;

	}

	public Vector3 add(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vector3 add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3 nAdd(Vector3 v) {
		return new Vector3(x + v.x, y + v.y, z + v.z);
	}

	public Vector3 nAdd(double x, double y, double z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 sub(Vector3 v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public Vector3 sub(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vector3 nSub(Vector3 v) {
		return new Vector3(x - v.x, y - v.y, z - v.z);
	}

	public Vector3 nSub(double x, double y, double z) {
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}

	public Vector3 scale(double s) {
		x *= s;
		y *= s;
		z *= s;
		return this;
	}

	public Vector3 nScale(double s) {
		return new Vector3(x * s, y * s, z * s);
	}

	public Vector3 scaleAdd(double s, Vector3 v) {
		x += s * v.x;
		y += s * v.y;
		z += s * v.z;
		return this;
	}

	public Vector3 scaleAdd(double s, double x, double y, double z) {
		this.x += s * x;
		this.y += s * y;
		this.z += s * z;
		return this;
	}

	public Vector3 nScaleAdd(double s, Vector3 v) {
		return new Vector3(x + s * v.x, y + s * v.y, z + s * v.z);
	}

	public Vector3 nScaleAdd(double s, double x, double y, double z) {
		return new Vector3(this.x + s * x, this.y + s * y, this.z + s * z);
	}

	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3 cross(Vector3 v) {
		double nx = y * v.z - z * v.y;
		double ny = z * v.x - x * v.z;
		z = x * v.y - y * v.x;
		x = nx;
		y = ny;
		return this;
	}

	public Vector3 nCross(Vector3 v) {
		return new Vector3(
				y * v.z - z * v.y,
				z * v.x - x * v.z,
				x * v.y - y * v.x);
	}

	/**
	 * This will generate a cross product Matrix (wX) of this vector (a) which
	 * can then be multiplied with another vector (b) to result in (a)x(b)
	 *
	 * @return The cross product matrix of this vector
	 */
	public Matrix3 crossProductMatrix() {
		return new Matrix3(0, -z, y,
				z, 0, -x,
				-y, x, 0);
	}

	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double mag2() {
		return x * x + y * y + z * z;
	}

	public Vector3 norm() {
		double s = 1. / Math.sqrt(x * x + y * y + z * z);
		x *= s;
		y *= s;
		z *= s;
		return this;
	}

	public Vector3 nNorm() {
		double s = 1. / Math.sqrt(x * x + y * y + z * z);
		return new Vector3(x * s, y * s, z * s);
	}

	public double angleBetween(Vector3 v) {
		Vector3 a = nNorm();
		Vector3 b = v.nNorm();
		return Math.acos(a.dot(b));
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3) {
			Vector3 v = (Vector3) obj;
			return x == v.x && y == v.y && z == v.z;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		return hash;
	}

	/**
	 * Multiplies each component (x, y, z) of the Vectors together
	 * i.e. x = a.x * b.x, y = a.y * b.y, ....
	 *
	 * @param v
	 * @return A new Vector3 containing the result of the component multiplication
	 */
	public Vector3 nCompMul(Vector3 v) {
		return new Vector3(x * v.x, y * v.y, z * v.z);
	}

	/**
	 * Multiplies each component (x, y, z) of the Vectors together
	 * i.e. x = a.x * b.x, y = a.y * b.y, ....
	 *
	 * Note: the result will be stored in the calling object
	 *
	 * @param v
	 * @return reference to (this) containing the result of the component multiplication
	 */
	public Vector3 compMul(Vector3 v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return this;
	}

	/**
	 * Rotates the vector around that axis by the amount specified
	 *
	 * @param axis - Axis of rotation, this should be a normalised unit vector
	 * @param theta - amount of rotation in radians
	 * @return new Vector containing the result
	 */
	public Vector3 nRotateAroundAxis(Vector3 axis, double theta) {
		double u = axis.x;
		double v = axis.y;
		double w = axis.z;

		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);

		double ms = axis.mag2();
		double m = Math.sqrt(ms);

		return new Vector3(
				((u * (u * x + v * y + w * z))
						+ (((x * (v * v + w * w)) - (u * (v * y + w * z))) * cTheta)
						+ (m * ((-w * y) + (v * z)) * sTheta)) / ms,
				((v * (u * x + v * y + w * z))
						+ (((y * (u * u + w * w)) - (v * (u * x + w * z))) * cTheta)
						+ (m * ((w * x) - (u * z)) * sTheta)) / ms,
				((w * (u * x + v * y + w * z))
						+ (((z * (u * u + v * v)) - (w * (u * x + v * y))) * cTheta)
						+ (m * (-(v * x) + (u * y)) * sTheta)) / ms);
	}

	public boolean isUndefined() {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z);
	}

	/**
	 * Rotates the vector around that axis by the amount specified
	 *
	 * @param axis - Axis of rotation, this should be a normalised unit vector
	 * @param theta - amount of rotation in radians
	 * @return new Vector containing the result
	 */
	public Vector3 rotateAroundAxis(Vector3 axis, double theta) {
		double u = axis.x;
		double v = axis.y;
		double w = axis.z;

		double cTheta = Math.cos(theta);
		double sTheta = Math.sin(theta);

		double ms = axis.mag2();
		double m = Math.sqrt(ms);


		double x1 = ((u * (u * x + v * y + w * z))
				+ (((x * (v * v + w * w)) - (u * (v * y + w * z))) * cTheta)
				+ (m * ((-w * y) + (v * z)) * sTheta)) / ms;

		double y1 = ((v * (u * x + v * y + w * z))
				+ (((y * (u * u + w * w)) - (v * (u * x + w * z))) * cTheta)
				+ (m * ((w * x) - (u * z)) * sTheta)) / ms;

		double z1 = ((w * (u * x + v * y + w * z))
				+ (((z * (u * u + v * v)) - (w * (u * x + v * y))) * cTheta)
				+ (m * (-(v * x) + (u * y)) * sTheta)) / ms;

		x = x1;
		y = y1;
		z = z1;
		return this;
	}

	public Vector3 rotateX(double theta) {
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		double y1 = c * y + s * z;
		double z1 = -s * y + c * z;
		y = y1;
		z = z1;
		return this;
	}

	public Vector3 nRotateX(double theta) {
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		return new Vector3(
				x,
				c * y + s * z,
				-s * y + c * z);
	}

	public Vector3 rotateY(double theta) {
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		double x1 = c * x - s * z;
		double z1 = s * x + c * z;
		x = x1;
		z = z1;
		return this;
	}

	public Vector3 nRotateY(double theta) {
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		return new Vector3(
				c * x - s * z,
				y,
				s * x + c * z);
	}

	public Vector3 rotateZ(double theta) {
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		double x1 = c * x + s * y;
		double y1 = -s * x + c * y;
		x = x1;
		y = y1;
		return this;
	}

	public Vector3 nRotateZ(double theta) {
		double c = Math.cos(theta);
		double s = Math.sin(theta);

		return new Vector3(
				c * x + s * y,
				-s * x + c * y,
				z);
	}
}
