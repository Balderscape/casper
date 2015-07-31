package au.com.clearboxsystems.casper.gl.shader.block;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.Vector4;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: pauls
 * Timestamp: 20/01/14 12:04 PM
 */
public class Material {
	public Vector4 diffuseColour;
	public Vector4 specularColour;
	public float specularShininess;

	private static Map<Integer, Material> materialCache = new HashMap<>();

	public static Material fromColour(Color color) {
		if (!materialCache.containsKey(color.getRGB())) {
			Material m = new Material();
			m.diffuseColour = new Vector4(color.getRed() / 255., color.getGreen() / 255., color.getBlue() / 255., 1.);
			m.specularColour = new Vector4(color.getRed() / 255., color.getGreen() / 255., color.getBlue() / 255., 1.);
			m.specularShininess = 0.2f;
			materialCache.put(color.getRGB(), m);
			return m;
		}

		return materialCache.get(color.getRGB());
	}
}
