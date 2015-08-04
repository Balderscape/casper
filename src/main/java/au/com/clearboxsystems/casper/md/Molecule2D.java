package au.com.clearboxsystems.casper.md;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shape.Sphere;
import au.com.clearboxsystems.casper.math.Vector2;
import au.com.clearboxsystems.casper.math.Vector3;

/**
 * User: pauls
 * Timestamp: 3/08/2015 2:41 PM
 */
public class Molecule2D extends Sphere {
	public Vector2 position = new Vector2();
	public Vector2 velocity;
	public Vector2 acceleration;

	public Molecule2D() {
		this.radius = 1.8;
	}

	@Override
	public Vector3 getPosition() {
		return new Vector3(0, position.x, position.y);
	}
}
