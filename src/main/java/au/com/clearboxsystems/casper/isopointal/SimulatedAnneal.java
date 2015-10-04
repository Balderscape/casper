package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 16/09/15.
 */
public class SimulatedAnneal {

	public double findMinimumEnergy(int numRuns, int trialsPerRun, int spaceGroup, String[] wyckoffSites, double A, double beta) {
		double startkT = 2;
		double endT = 0.01;

		IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();
		double minEnergy = Double.MAX_VALUE;
		for (int i = 0; i < numRuns; i++) {
			IsopointalSet isopointalSet = isopointalSetFactory.getIsopointalSet(spaceGroup, wyckoffSites);
			double energy = runSimulatedAnneal(trialsPerRun, 2, 0.01, isopointalSet, A, beta);
			if (energy < minEnergy)
				minEnergy = energy;
		}

		return minEnergy;
	}

	public double runSimulatedAnneal(int numTrials, double startkT, double endkT, IsopointalSet isopointalSet, double A, double beta) {
		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential(A, beta);

		double numAtoms = isopointalSet.getNumPositions();

		isopointalSet.updateRandomVariable(1);
		double energy = pot.computeEnergy(isopointalSet);
		double lastEnergy = energy;
		double minEnergy = Double.MAX_VALUE;

		double kT = startkT;
		double geometricStep = Math.pow(endkT/startkT, 1.0/numTrials);
		for (int i = 0; i < numTrials; i++) {
			kT *= geometricStep;
//			if (i % 10000 == 0)
//				System.out.println("kT = " + kT + ", Energy = " + lastEnergy);

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
//				rejectCount++;
			}
		}

		return minEnergy / numAtoms;
	}
}
