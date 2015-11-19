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
public class LJEAM_Peter_Potential {
	public final double rhobar0;     // Nearest neighbour interactions (ref state)
	public final double A;    // Amount of multi-body bonding
	public final double beta=2.0;      // Decay of electron density

	public final double Rcutoff = 6.0;      // Compromise that we reached for the run...

	private final double RcutoffSq;
	private final double AOnrhobar0;

	private List<Double> interAtomicDistances[];
	private int[] multiplicity;

	public LJEAM_Peter_Potential() {
		this(1, 10);
	}

	public LJEAM_Peter_Potential(double a, double rhobar0) {
		this.rhobar0 = rhobar0;
		A = a;

		RcutoffSq = Rcutoff * Rcutoff;
		AOnrhobar0 = A / rhobar0;
	}

	public double computeEnergy(IsopointalSet isopointalSet) {
		double totalEnergy = 0;

		generateInterAtomDistances(isopointalSet);

		for (int i = 0; i < isopointalSet.getNumPositions(); i++) {
			double energy = computeF(computeRhoSum(interAtomicDistances[i]));

			double phiSum = 0;
			for (double dist : interAtomicDistances[i]) {
				phiSum += computeLJ(dist);
			}
			energy += 0.5 * phiSum;

			int mult = multiplicity[i];
			totalEnergy += mult * energy;
			i += (mult - 1); // Skip the positions with same energy;
		}

		return totalEnergy;
	}

	private void generateInterAtomDistances(IsopointalSet isopointalSet) {
		int numAtoms = isopointalSet.getNumPositions();
		interAtomicDistances = new List[numAtoms];
		multiplicity = new int[numAtoms];

		int dz = (int)Math.ceil(Rcutoff / isopointalSet.vecC.z);
		int dy = (int)Math.ceil((Rcutoff + Math.abs(dz * isopointalSet.vecC.y)) / isopointalSet.vecB.y);
		int dx = (int)Math.ceil((Rcutoff + Math.abs(dz * isopointalSet.vecC.x) + Math.abs(dy * isopointalSet.vecB.x)) / isopointalSet.vecA.x);

		for (int i = 0; i < numAtoms; i++) {
			interAtomicDistances[i] = new ArrayList<>();
			for (int j = 0; j < numAtoms; j++) {


				for (int x = -dx; x <= dx; x++) {
					for (int y = -dy; y <= dy; y++) {
						for (int z = -dz; z <= dz; z++) {
							if (x == 0 && y == 0 && z == 0 && i == j)
								continue;

							double r = isopointalSet.getDistSqBetweenPositions(i, j, x, y, z);
							if (r <= RcutoffSq)
								interAtomicDistances[i].add(Math.sqrt(r));
						}
					}
				}
			}
			multiplicity[i] = isopointalSet.getMultiplicity(i);
			i += (multiplicity[i]-1);
		}
	}

	private double computeF(double rhoBar) {
		if (rhoBar == 0) // Limit as rhoBar -> 0 = 0
			return 0;
		return AOnrhobar0 * rhoBar * (Math.log(rhoBar/rhobar0) - 1);
	}

	private double computeRhoSum(List<Double> interAtomicDistances) {
		double rhoSum = 0;
		for (double dist : interAtomicDistances) {
			rhoSum += computeRho(dist);
		}
		return rhoSum;
	}


	private double computeRho(double r) {
		return Math.exp(-beta*(r/Math.pow(2.0,1.0/6.0) - 1.0));
	}

	private double computeLJ(double r) {
		// note Peter's version is different to Baskes' version.
		double r6;
		r6=r*r;
		r6=r6*r6*r6;
		return 4.0/(r6*r6)-4.0/r6;
	}

	public static void main(String[] args) {
		LJEAM_Peter_Potential pot = new LJEAM_Peter_Potential();
	}
}
