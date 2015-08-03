package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 3/08/2015 2:24 PM
 */
public class Vector2I {
	public int x;
	public int y;

	public Vector2I() {
	}

	public Vector2I(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int product() {
		return x * y;
	}

	public Vector2 nScale(double s) {
		return new Vector2(s * x, s * y);
	}
}
