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
public abstract class Sphere {

	public abstract Vector3 getPosition();
	public double radius = 1;

	private Color color = Color.WHITE;
	public Vector4 material = new Vector4(1, 1, 1, 1);

	public void setColor(Color color) {
		this.color = color;
		material.set(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0);
	}
}
