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
	public final double Z0;     // Nearest neighbour interactions (ref state)
	public final double A;    // Amount of multi-body bonding
	public final double beta;      // Decay of electron density

//	public final double Rcutoff = 1.4;      // Atoms further away than this value are ignored
	public final double Rcutoff = 1.65;      // Compromise that we reached for the run...
//	public final double Rcutoff = 2.5;      // Atoms further away than this value are ignored

	private final double RcutoffSq;
	private final double invZ0;
	private final double AZ0On2;
	private final double minus2OnZ0;

	private List<Double> interAtomicDistances[];
	private int[] multiplicity;

	public LJEmbeddedAtomPotential() {
		this(0.5, 4, 12);
	}

	public LJEmbeddedAtomPotential(double a, double beta, double Z0) {
		this.Z0 = Z0;
		A = a;
		this.beta = beta;

		RcutoffSq = Rcutoff * Rcutoff;

		invZ0 = 1.0 / Z0;
		AZ0On2 = A * Z0 / 2.0;
		minus2OnZ0 = -2.0 / Z0;
	}

	public double computeEnergy(IsopointalSet isopointalSet) {
		double totalEnergy = 0;

		generateInterAtomDistances(isopointalSet);

		for (int i = 0; i < isopointalSet.getNumPositions(); i++) {
			double energy = computeF(computeRhoBar(interAtomicDistances[i]));

			double phiSum = 0;
			for (double dist : interAtomicDistances[i]) {
				phiSum += computePhi(dist);
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
		return AZ0On2 * rhoBar * (Math.log(rhoBar) - 1);
	}

	private double computePhi(double r) {
		return computeLJ(r) + minus2OnZ0 * computeF(computeRho(r));
	}

	private double computeRhoBar(List<Double> interAtomicDistances) {
		double rhoSum = 0;
		for (double dist : interAtomicDistances) {
			rhoSum += computeRho(dist);
		}
		return rhoSum  * invZ0;
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

		public static void main(String[] args) {
		LJEmbeddedAtomPotential pot = new LJEmbeddedAtomPotential();
		System.out.println("LJ(2): " + pot.computeLJ(4));
		System.out.println("LJ(1): " + pot.computeLJ(2));
	}
}
