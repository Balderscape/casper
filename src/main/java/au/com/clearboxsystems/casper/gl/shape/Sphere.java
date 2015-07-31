package au.com.clearboxsystems.casper.gl.shape;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.Vector3;
import au.com.clearboxsystems.casper.math.Vector4;

import java.awt.*;

/**
 * User: pauls
 * Timestamp: 31/07/2015 11:02 AM
 */
public class Sphere {

	public Vector3 position = new Vector3();
	public double radius = 1;

	public Color color = Color.BLUE;
	public Vector4 material = new Vector4(1, 1, 1, 1);
}
