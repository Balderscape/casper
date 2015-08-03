package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 6/01/14 2:03 PM
 */
public class Vector2 {
	public double x, y;

	public Vector2() {
	}

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public static Vector2 rand(double scale) {
		double theta = Math.random() * 2.0 * Math.PI;
		return new Vector2(Math.cos(theta) * scale, Math.sin(theta) * scale);
	}

	public Vector2 set(double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 nDiv(Vector2 v) {
		return new Vector2(x / v.x, y / v.y);
	}

	public Vector2 nDiv(Vector2I v) {
		return new Vector2(x / (double)v.x, y / (double)v.y);
	}

	public Vector2 times(Vector2 v) {
		x *= v.x;
		y *= v.y;
		return this;
	}

	public Vector2 sAdd(double scale, Vector2 v) {
		x += scale * v.x;
		y += scale * v.y;
		return this;
	}

	public Vector2 add(double s) {
		x += s;
		y += s;
		return this;
	}

	public Vector2 add(Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 scale(double s) {
		x *= s;
		y *= s;
		return this;
	}

	public Vector2 wrap(Vector2 region) {
		while (x >= 0.5 * region.x)
			x -= region.x;
		while (x < -0.5 * region.x)
			x += region.x;

		while (y >= 0.5 * region.y)
			y -= region.y;
		while (y < -0.5 * region.y)
			y += region.y;

		return this;
	}

	public Vector2 nSub(Vector2 v) {
		return new Vector2(x - v.x, y - v.y);
	}

	public double dot(Vector2 v) {
		return x * v.x + y * v.y;
	}
}
