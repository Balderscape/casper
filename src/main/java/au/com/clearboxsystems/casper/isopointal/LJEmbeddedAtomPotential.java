package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the Lennard-Jones Embedded-Atom Potential as defined in the paper
 * "Many-Body Effects in fcc Metals: A Lennard-Jones Embedded-Atom Potential"
 * 1999, M. I. Baskes
 *
 * User: pauls
 * Timestamp: 10/08/2015 10:41 AM
 */
public class LJEmbeddedAtomPotential {
	public double Z0       = 12;     // Nearest neighbour interactions (ref state)
	public double A        = 0.5;    // Amount of multi-body bonding
	public double beta     = 4;      // Decay of electron density

	public double Rcutoff = 2;      // Atoms further away than this value are ignored

	private List<Double> interAtomicDistances[];

	public double computeEnergy(IsopointalSet isopointalSet) {
		double totalEnergy = 0;

		generateInterAtomDistances(isopointalSet);

		for (int i = 0; i < isopointalSet.getNumPositions(); i++) {
			totalEnergy += computeF(computeRhoBar(interAtomicDistances[i]));

			double phiSum = 0;
			for (double dist : interAtomicDistances[i]) {
				phiSum += computePhi(dist);
			}
			totalEnergy += 0.5 * phiSum;
		}

		return totalEnergy;
	}

	private void generateInterAtomDistances(IsopointalSet isopointalSet) {
		int numAtoms = isopointalSet.getNumPositions();
		interAtomicDistances = new List[numAtoms];
		for (int i = 0; i < numAtoms; i++) {
			interAtomicDistances[i] = new ArrayList<>();
			for (int j = 0; j < numAtoms; j++) {

				int dx = (int)Math.ceil(Rcutoff / isopointalSet.spaceGroup.a);
				int dy = (int)Math.ceil(Rcutoff / isopointalSet.spaceGroup.b);
				int dz = (int)Math.ceil(Rcutoff / isopointalSet.spaceGroup.c);

				for (int x = -dx; x <= dx; x++) {
					for (int y = -dy; y <= dy; y++) {
						for (int z = -dz; z <= dz; z++) {
							if (x == 0 && y == 0 && z == 0 && i == j)
								continue;

							double r = isopointalSet.getDistBetweenPositions(i, j, x, y, z);
							if (r <= Rcutoff)
								interAtomicDistances[i].add(r);
						}
					}
				}
			}
		}
	}

	private double computeF(double rhoBar) {
		if (rhoBar == 0) // Limit as rhoBar -> 0 = 0
			return 0;
		return (A * Z0 / 2.0) * rhoBar * (Math.log(rhoBar) - 1);
	}

	private double computePhi(double r) {
		return computeLJ(r) - (2.0 / Z0) * computeF(computeRho(r));
	}

	private double computeRhoBar(List<Double> interAtomicDistances) {
		double rhoSum = 0;
		for (double dist : interAtomicDistances) {
			rhoSum += computeRho(dist);
		}
		return rhoSum / Z0;
	}


	private double computeRho(double r) {
		return Math.exp(-beta*(r - 1.0));
	}

	private double computeLJ(double r) {
		double r6;
		r6=r*r;
		r6=r6*r6*r6;
		return 1.0/(r6*r6)-2.0/r6;
	}
}
