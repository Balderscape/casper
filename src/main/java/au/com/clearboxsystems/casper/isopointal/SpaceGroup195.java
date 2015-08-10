package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Some initial dabbling with space groups... Will be generalised...
 *
 * User: pauls
 * Timestamp: 10/08/2015 11:08 AM
 */
public class SpaceGroup195 {

	// There is only one degree of freedom in the unit cell, alpha, beta and gamma = 90 deg. and a = b = c
	public double a = 1;
	public List<WyckoffSite> wyckoffSites;

	public SpaceGroup195() {
		createWycoffPositions();
	}

	private void createWycoffPositions() {
		wyckoffSites = new ArrayList<>();
		wyckoffSites.add(new WyckoffSite("a", new Vector3()));
		wyckoffSites.add(new WyckoffSite("b", new Vector3(0.5, 0.5, 0.5)));
		wyckoffSites.add(new WyckoffSite("c", new Vector3(0, 0.5, 0.5),
														new Vector3(0.5, 0, 0.5),
														new Vector3(0.5, 0.5, 0)));
		wyckoffSites.add(new WyckoffSite("d", new Vector3(0.5, 0, 0),
														new Vector3(0, 0.5, 0),
														new Vector3(0, 0, 0.5)));

	}
}
