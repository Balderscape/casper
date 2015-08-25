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

	public double computeEnergy(List<Vector3> positions) {
		double totalEnergy = 0;

		for (int i = 0; i < positions.size(); i++) {
			Vector3 ai = positions.get(i);
			totalEnergy += computeF(computeRhoBar(ai, positions));

			double phiSum = 0;
			for (int j = 0; j < positions.size(); j++) {
				if (j == i)
					continue;

				Vector3 aj = positions.get(j);
				double r = aj.dist(ai);
				phiSum += computePhi(r);
			}
			totalEnergy += 0.5 * phiSum;
		}

		return totalEnergy;
	}

	private double computeF(double rhoBar) {
		return (A * Z0 / 2.0) * rhoBar * (Math.log(rhoBar) - 1);
	}

	private double computePhi(double r) {
		return computeLJ(r) - (2.0 / Z0) * computeF(computeRho(r));
	}

	private double computeRhoBar(Vector3 ai, List<Vector3> positions) {
		double rhoSum = 0;
		for (int j = 0; j < positions.size(); j++) {
			Vector3 aj = positions.get(j);
			if (aj == ai)
				continue;

			rhoSum += computeRho(ai.dist(aj));
		}
		return rhoSum / Z0;
	}


	private double computeRho(double r) {
		return Math.exp(-beta*(r - 1.0));
	}

	private double computeLJ(double r) {
		return Math.pow(r, -12.0) - 2 * Math.pow(r, -6.0);
	}
}
