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
		double startkT = 10;
		double endkT = 0.001;

		IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

		IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(16, new String[] {"a", "j", "m"});
		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential();

		isopointalSet.updateRandomVariable(startkT);
		double energy = pot.computeEnergy(isopointalSet);

		double lastEnergy = energy;
		int rejectCount = 0;
		int TOTAL_TRIALS = 1000000;
		double kT = startkT;
		for (int i = 0; i < TOTAL_TRIALS; i++) {
			kT *= 0.99999;
			if (i % 10000 == 0)
				System.out.println("kT = " + kT + ", Energy = " + lastEnergy);

			isopointalSet.updateRandomVariable(kT);
			energy = pot.computeEnergy(isopointalSet);

			double deltaEnergy = energy - lastEnergy;
//			if (i % 10000 == 0)
//				System.out.println("delta = " + deltaEnergy + ", e = " + Math.exp(-deltaEnergy / kT));
			if (deltaEnergy != 0 && (deltaEnergy < 0 || isopointalSet.random.nextDouble() < Math.exp(-deltaEnergy / kT))) {
				// Accept
				lastEnergy = energy;
			} else {
				// Reject
				isopointalSet.revertLastUpdate();
				rejectCount++;
			}
		}
		System.out.println("Final Energy = " + lastEnergy);
		System.out.println("Rejection Ratio = " + 100.0 * rejectCount / TOTAL_TRIALS);
		System.out.println(isopointalSet);
	}
}