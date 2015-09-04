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
		double startkT = 1;
		double endkT = 0.001;

		IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

		IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(16, new String[] {"a", "j", "m"});
		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential();

		isopointalSet.updateRandomVariable();
		double energy = pot.computeEnergy(isopointalSet);

		double lastEnergy = energy;
		int rejectCount = 0;
		int TOTAL_TRIALS = 1000000;
		for (int i = 0; i < TOTAL_TRIALS; i++) {
			// FIXME: Something is wrong with this line but I cannot see what.... Needs to be geometric!
			double kT = startkT + ((endkT - startkT) * i / TOTAL_TRIALS);
			if (i % 10000 == 0)
				System.out.println("kT = " + kT + ", Energy = " + lastEnergy);

			isopointalSet.updateRandomVariable();
			energy = pot.computeEnergy(isopointalSet);

			double deltaEnergy = energy - lastEnergy;
			if (deltaEnergy < 0 || isopointalSet.random.nextDouble() < Math.exp(-deltaEnergy / kT)) {
				// Accept
				lastEnergy = deltaEnergy;
			} else {
				// Reject
				isopointalSet.revertLastUpdate();
				rejectCount++;
			}
		}
		System.out.println("Final Energy = " + energy);
		System.out.println("Rejection Ratio = " + 100.0 * rejectCount / TOTAL_TRIALS);
		System.out.println(isopointalSet);
	}
}