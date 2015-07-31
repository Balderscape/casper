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

	public Vector2 nDiv(Vector2 v) {
		return new Vector2(x / v.x, y / v.y);
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
}
