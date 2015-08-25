package au.com.clearboxsystems.casper.isopointal;

import au.com.clearboxsystems.casper.math.Vector3;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 25/08/2015 9:54 AM
 */
public class IsopointalSetFactoryTest {

	@Test
	public void testGetIsopointalSet() throws Exception {
		IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

		IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(16, new String[] {"a", "j", "m"});

		List<Vector3> positions = isopointalSet.getPositions();

		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential();

		double energy = pot.computeEnergy(positions);

		System.out.println("Energy = " + energy);

	}
}