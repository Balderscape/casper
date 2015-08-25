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

	Random random = new Random(1);

	private String name;
	private SpaceGroup spaceGroup;
	private List<WyckoffSite> wyckoffSites;

	private Variable[] basis;
	private int wyckoffStartIdx;

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
	}

	// Step Size = 0.01 in the wallpaper code....
	private static final double STEP_SIZE = 0.01;
	private int lastUpdateIdx;
	public void updateRandomVariable() {
		int idx = random.nextInt(basis.length);
		basis[idx].update(STEP_SIZE);
		lastUpdateIdx = idx;
	}

	public void revertLastUpdate() {
		basis[lastUpdateIdx].revert();
	}

	public List<Vector3> getPositions() {
		List<Vector3> positions = new ArrayList<>();

		spaceGroup.updateBasis(basis);
		int basisIdx = wyckoffStartIdx;
		for (WyckoffSite site : wyckoffSites) {
			Vector3 posVariable = new Vector3();
			if (site.hasX)
				posVariable.x = basis[basisIdx++].curVal;
			if (site.hasY)
				posVariable.y = basis[basisIdx++].curVal;
			if (site.hasZ)
				posVariable.z = basis[basisIdx++].curVal;

			for (WyckoffPosition wyckoffPosition : site.positions) {
				positions.add(wyckoffPosition.getPosition(posVariable));
			}

			// FIXME: These positions need to be transformed into the unit cell basis.
		}

		// FIXME: This is just for one unit cell....

		return positions;

	}


	@Override
	public String toString() {
		return "IsopointalSet{" +
				"name='" + name + '\'' +
				", spaceGroup=" + spaceGroup +
				", wyckoffSites=" + wyckoffSites +
				'}';
	}
}
