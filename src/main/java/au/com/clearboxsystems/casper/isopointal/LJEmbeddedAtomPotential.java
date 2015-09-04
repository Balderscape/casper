package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.Vector3;
import au.com.clearboxsystems.casper.model.Atom;

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

	public double computeEnergy(IsopointalSet isopointalSet) {
		double totalEnergy = 0;

		for (int i = 0; i < isopointalSet.getNumPositions(); i++) {
//			System.out.println("F = " + computeF(computeRhoBar(i, isopointalSet)));
			totalEnergy += computeF(computeRhoBar(i, isopointalSet));

			double phiSum = 0;
			for (int j = 0; j < isopointalSet.getNumPositions(); j++) {
				if (j == i)
					continue;

				double r = isopointalSet.getDistBetweenPositions(i, j);
//				System.out.println("r = " + r);
//				System.out.println("phi = " + computePhi(r));
				phiSum += computePhi(r);
			}
			totalEnergy += 0.5 * phiSum;
		}

		return totalEnergy;
	}

	private double computeF(double rhoBar) {
		if (rhoBar == 0) // Limit as rhoBar -> 0 = 0
			return 0;
		return (A * Z0 / 2.0) * rhoBar * (Math.log(rhoBar) - 1);
	}

	private double computePhi(double r) {
//		System.out.println("LJ = " + computeLJ(r));
//		System.out.println("Rho = " + computeRho(r));
//		System.out.println("F = " + computeF(computeRho(r)));
		return computeLJ(r) - (2.0 / Z0) * computeF(computeRho(r));
	}

	private double computeRhoBar(int i, IsopointalSet isopointalSet) {
		double rhoSum = 0;
		for (int j = 0; j < isopointalSet.getNumPositions(); j++) {
			if (j == i)
				continue;

			rhoSum += computeRho(isopointalSet.getDistBetweenPositions(i, j));
		}
		return rhoSum / Z0;
	}


	private double computeRho(double r) {
//		System.out.println("e^" + (-beta*(r - 1.0)));
		return Math.exp(-beta*(r - 1.0));
	}

	private double computeLJ(double r) {
		//return Math.pow(r, -12.0) - 2 * Math.pow(r, -6.0);
		double r6;
		r6=r*r;
		r6=r6*r6*r6;
		return 1.0/(r6*r6)-2.0/r6;
	}
}
