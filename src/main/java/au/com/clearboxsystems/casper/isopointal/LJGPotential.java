package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the Lennard-Jones Gaussian ...
 *
 * User: patrick
 * Timestamp: 10/08/2015 10:41 AM
 */
public class LJGPotential {
	public final double epsilon;     // Nearest neighbour interactions (ref state)
	public final double r0;    // Amount of multi-body bonding
	public final double sigmasq;      // Decay of electron density

	public final double Rcutoff = 3.0;      // Atoms further away than this value are ignored

	private final double RcutoffSq;
	private final double s;

	private List<Double> interAtomicDistances[];
	private int[] multiplicity;

	public LJGPotential() {
		this(1.0,0.0,0.0);
	}

	public LJGPotential(double r0, double epsilon, double sigmasq) {
		this.r0 = r0;
		this.epsilon = epsilon;
		this.sigmasq = sigmasq;

		RcutoffSq = Rcutoff * Rcutoff;
		s = 2.0*sigmasq;
	}

	public double computeEnergy(IsopointalSet isopointalSet) {
		double totalEnergy = 0;

		generateInterAtomDistances(isopointalSet);

		for (int i = 0; i < isopointalSet.getNumPositions(); i++) {
			double energy = 0.0;

			double phiSum = 0;
			for (double dist : interAtomicDistances[i]) {
				phiSum += computeLJG(dist);
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

	private double computeLJG(double r) {

		// calculate lennard jones potential
		double r6;
		r6=r*r;
		r6=r6*r6*r6;

		double lj = 1.0/(r6*r6)-2.0/r6;

		// calculate Gaussian term
		double r2 = r-r0;
		r2 *= r2;

		return lj-epsilon*Math.exp(-r2/s);
	}


	public static void main(String[] args) {
		LJGPotential pot = new LJGPotential();
		//System.out.println("LJ(2): " + pot.computeLJG(4));
		//System.out.println("LJ(1): " + pot.computeLJG(2));
	}
}
