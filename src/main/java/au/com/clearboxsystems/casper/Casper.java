package au.com.clearboxsystems.casper;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shape.Sphere;
import au.com.clearboxsystems.casper.md.Molecule2D;
import com.jogamp.opengl.GLProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: pauls
 * Timestamp: 30/07/2015 9:53 AM
 */
public class Casper {
	public static final Logger logger = LoggerFactory.getLogger(Casper.class);

	public static void main(String[] args) {
		logger.info("Starting Casper...");

		GLProfile.initSingleton();

		Stage stage = new Stage();
		stage.setExitOnClose(true);

		Sphere sphere = new Molecule2D();
		sphere.radius = 1;
		stage.addSphere(sphere);

	}

}
