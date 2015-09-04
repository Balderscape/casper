package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: pauls
 * Timestamp: 10/08/2015 12:36 PM
 */
public class IsopointalSet {

	Random random = new Random();

	private String name;
	protected SpaceGroup spaceGroup;
	private List<WyckoffSite> wyckoffSites;
	private List<Vector3> wyckoffPositions;

	private Variable[] basis;
	private int wyckoffStartIdx;

	private int numPositions;

	public IsopointalSet(String name, SpaceGroup spaceGroup, List<WyckoffSite> wyckoffSites) {
		this.name = name;
		this.spaceGroup = spaceGroup;
		this.wyckoffSites = wyckoffSites;

		int linearVariables = spaceGroup.getLinearDegreesOfFreedom();
		int angularVariables = spaceGroup.getAngularDegreesOfFreedom();
		int relativeVariables = 0;

		for (WyckoffSite site : wyckoffSites) {
			relativeVariables += site.getDegreesOfFreedom();
		}

		basis = new Variable[linearVariables + angularVariables + relativeVariables];
		int b = 0;
		for (int i = 0; i < linearVariables; i++) {
			basis[b++] = new LinearVariable(random);
		}
		for (int i = 0; i < angularVariables; i++) {
			basis[b++] = new AngularVariable(random);
		}
		for (int i = 0; i < relativeVariables; i++) {
			basis[b++] = new RelativeVariable(random);
		}
		wyckoffStartIdx = linearVariables + angularVariables;

		wyckoffPositions = new ArrayList<>();
		for (WyckoffSite site : wyckoffSites) {
			for (WyckoffPosition position : site.positions) {
				wyckoffPositions.add(new Vector3());
			}
		}
		numPositions = wyckoffPositions.size();
	}

	// Step Size = 0.01 in the wallpaper code....
	private static final double STEP_SIZE = 0.01;
	private int lastUpdateIdx;
	public void updateRandomVariable(double temperature) {
		int idx = random.nextInt(basis.length);
		basis[idx].update(temperature * STEP_SIZE);
		lastUpdateIdx = idx;

		updateBasisAndPositions();
	}

	public void revertLastUpdate() {
		basis[lastUpdateIdx].revert();
	}

	void updateBasisAndPositions() {
		spaceGroup.updateBasis(basis);

		int basisIdx = wyckoffStartIdx;
		int posIdx = 0;
		for (WyckoffSite site : wyckoffSites) {
			Vector3 posVariable = new Vector3();
			if (site.hasX)
				posVariable.x = basis[basisIdx++].curVal;
			if (site.hasY)
				posVariable.y = basis[basisIdx++].curVal;
			if (site.hasZ)
				posVariable.z = basis[basisIdx++].curVal;

			for (WyckoffPosition wyckoffPosition : site.positions) {
				wyckoffPositions.get(posIdx++).set(wyckoffPosition.getPosition(posVariable));
			}
		}
	}

	public int getNumPositions() {
		return numPositions;
	}

	public double getDistBetweenPositions(int idx1, int idx2, int xOff, int yOff, int zOff) {
		Vector3 pos1 = wyckoffPositions.get(idx1);
		Vector3 pos2 = wyckoffPositions.get(idx2);

		double dx = pos1.x - pos2.x + xOff;
		double dy = pos1.y - pos2.y + yOff;
		double dz = pos1.z - pos2.z + zOff;

		return Math.sqrt(dx * dx * spaceGroup.a * spaceGroup.a +
								dy * dy * spaceGroup.b * spaceGroup.b +
								dz * dz * spaceGroup.c * spaceGroup.c +
								2 * dx * dy * spaceGroup.a * spaceGroup.b * Math.cos(spaceGroup.gamma) +
								2 * dy * dz * spaceGroup.b * spaceGroup.c * Math.cos(spaceGroup.alpha) +
								2 * dz * dx * spaceGroup.c * spaceGroup.a * Math.cos(spaceGroup.beta));
	}

	@Override
	public String toString() {
		return "IsopointalSet{" +
				"name='" + name + '\'' +
				", spaceGroup=" + spaceGroup +
				", positions=" + wyckoffPositions +
				'}';
	}
}
