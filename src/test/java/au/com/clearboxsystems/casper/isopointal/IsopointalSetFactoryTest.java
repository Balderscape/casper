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
		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential();

		isopointalSet.updateRandomVariable();
		double energy = pot.computeEnergy(isopointalSet);
		System.out.println("Starting Energy = " + energy);

		double lastEnergy = energy;
		int rejectCount = 0;
		int TOTAL_TRIALS = 1000000;
		for (int i = 0; i < TOTAL_TRIALS; i++) {
			isopointalSet.updateRandomVariable();
			energy = pot.computeEnergy(isopointalSet);
			if (energy > lastEnergy) {
				isopointalSet.revertLastUpdate();
//				System.out.println("Energy = " + energy + " - REJECTED");
				rejectCount++;
			} else {
				lastEnergy = energy;
//				System.out.println("Energy = " + energy + " - ACCEPTED");
			}
		}
		System.out.println("Final Energy = " + energy);
		System.out.println("Rejection Ratio = " + 100.0 * rejectCount / TOTAL_TRIALS);
		System.out.println(isopointalSet);
	}
}