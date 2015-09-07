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
		double startkT = 2;
		double endT = 0.01;

		int TOTAL_TRIALS = 1000000;

		double geometricStep = Math.pow(endT/startkT, 1.0/TOTAL_TRIALS);
		System.out.println("Step size: " + geometricStep);

		IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

//		IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(16, new String[]{"a", "j", "m"});
		IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(225, new String[]{"a"});
		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential();

		isopointalSet.updateRandomVariable(startkT);
		double energy = pot.computeEnergy(isopointalSet);

		double lastEnergy = energy;
		double minEnergy = 0;

		int rejectCount = 0;
		double kT = startkT;
		for (int i = 0; i < TOTAL_TRIALS; i++) {
			kT *= geometricStep;
			if (i % 10000 == 0)
				System.out.println("kT = " + kT + ", Energy = " + lastEnergy);

			isopointalSet.updateRandomVariable(1);
			energy = pot.computeEnergy(isopointalSet);

			double deltaEnergy = energy - lastEnergy;
//			if (i % 10000 == 0)
//				System.out.println("delta = " + deltaEnergy + ", e = " + Math.exp(-deltaEnergy / kT));
			if ((deltaEnergy < 0 || isopointalSet.random.nextDouble() < Math.exp(-deltaEnergy / kT))) {
				// Accept
				lastEnergy = energy;
				if (lastEnergy < minEnergy)
					minEnergy = lastEnergy;
			} else {
				// Reject
				isopointalSet.revertLastUpdate();
				rejectCount++;
			}


		}
		System.out.println("Final Energy = " + lastEnergy);
		System.out.println("Rejection Ratio = " + 100.0 * rejectCount / TOTAL_TRIALS);
		System.out.println("Minimum Energy = " + minEnergy);
		System.out.println(isopointalSet);
	}
}