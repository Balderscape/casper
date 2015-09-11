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
//	private List<Vector3> wyckoffPositions;

	private Variable[] basis;
	private int wyckoffStartIdx;

	private int numPositions;

	private double a, b, c;
	private double cosAlpha;
	private double cosBeta;
	private double cosGamma;
	private double sinGamma;
	private double invSinGamma;
	private double unitVolume;

	private Vector3 vecA = new Vector3();
	private Vector3 vecB = new Vector3();
	private Vector3 vecC = new Vector3();

	private Vector3[] cartesianPositions;
	private int[] multiplicity;

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

		numPositions = 0;
		for (WyckoffSite site : wyckoffSites) {
			for (WyckoffPosition position : site.positions) {
				numPositions++;
			}
		}

		cartesianPositions = new Vector3[numPositions];
		multiplicity = new int[numPositions];

		for (int i = 0; i < numPositions; i++) {
			cartesianPositions[i] = new Vector3();
		}

		int idx = 0;
		for (WyckoffSite site : wyckoffSites) {
			int mult = site.positions.size();
			for (int i = 0; i < mult; i++)
				multiplicity[idx++] = mult;
		}
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

		a = spaceGroup.a;
		b = spaceGroup.b;
		c = spaceGroup.c;
		cosAlpha = Math.cos(spaceGroup.alpha);
		cosBeta = Math.cos(spaceGroup.beta);
		cosGamma = Math.cos(spaceGroup.gamma);
		sinGamma = Math.sin(spaceGroup.gamma);
		invSinGamma = 1.0 / sinGamma;
		unitVolume = Math.sqrt(1 - cosAlpha * cosAlpha - cosBeta * cosBeta - cosGamma * cosGamma + 2 * cosAlpha * cosBeta * cosGamma);

		transformToCartesian(new Vector3(1, 0, 0), vecA);
		transformToCartesian(new Vector3(0, 1, 0), vecB);
		transformToCartesian(new Vector3(0, 0, 1), vecC);

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

//			transformToCartesian(site.positions.get(0).getPosition(posVariable), cartesianPositions[posIdx++]);
			for (WyckoffPosition wyckoffPosition : site.positions) {
//				wyckoffPositions.get(posIdx++).set(wyckoffPosition.getPosition(posVariable));
				transformToCartesian(wyckoffPosition.getPosition(posVariable), cartesianPositions[posIdx++]);
			}
		}
	}

	public int getNumPositions() {
		return numPositions;
	}

	public int getMultiplicity(int position) {
		return multiplicity[position];
	}

	public double getDistSqBetweenPositions(int idx1, int idx2, int xOff, int yOff, int zOff) {
		Vector3 pos1 = cartesianPositions[idx1];
		Vector3 pos2 = cartesianPositions[idx2];
//
//		Vector3 distSq = pos1.nSub(pos2);
//		distSq.scaleAdd(-xOff, vecA);
//		distSq.scaleAdd(-yOff, vecB);
//		distSq.scaleAdd(-zOff, vecC);
//
//		return distSq.mag2();

		double dx = pos1.x - pos2.x - xOff * vecA.x - yOff * vecB.x - zOff * vecC.x;
		double dy = pos1.y - pos2.y - xOff * vecA.y - yOff * vecB.y - zOff * vecC.y;
		double dz = pos1.z - pos2.z - xOff * vecA.z - yOff * vecB.z - zOff * vecC.z;

		return dx * dx + dy * dy + dz * dz;


//		double dx = pos1.x - pos2.x + xOff;
//		double dy = pos1.y - pos2.y + yOff;
//		double dz = pos1.z - pos2.z + zOff;
//
//		return Math.sqrt(dx * dx * spaceGroup.a * spaceGroup.a +
//								dy * dy * spaceGroup.b * spaceGroup.b +
//								dz * dz * spaceGroup.c * spaceGroup.c +
//								2 * dx * dy * spaceGroup.a * spaceGroup.b * cosGamma +
//								2 * dy * dz * spaceGroup.b * spaceGroup.c * cosAlpha +
//								2 * dz * dx * spaceGroup.c * spaceGroup.a * cosBeta);
	}


	private void transformToCartesian(Vector3 relPos, Vector3 resultPos) {
		resultPos.x = relPos.x * a + relPos.y * b * cosGamma + relPos.z * c * cosBeta;
		resultPos.y = relPos.y * b * sinGamma + relPos.z * c * (cosAlpha - cosBeta * cosGamma) * invSinGamma;
		resultPos.z = relPos.z * c * unitVolume * invSinGamma;
	}

	@Override
	public String toString() {
		return "IsopointalSet{" +
				"name='" + name + '\'' +
				", spaceGroup=" + spaceGroup +
//				", positions=" + wyckoffPositions +
				'}';
	}
}
