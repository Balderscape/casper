package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.*;

import java.util.List;
import java.util.Random;

/**
 * User: pauls
 * Timestamp: 10/08/2015 12:36 PM
 */
public class IsopointalSet {

    public static final int NUM_SPACE_GROUPS = 230;

	Random random = new Random();

	public String name;
	protected SpaceGroup spaceGroup;
	private List<WyckoffSite> wyckoffSites;
//	private List<Vector3> wyckoffPositions;

	public Variable[] basis;
	private int wyckoffStartIdx;

	private int numPositions;

	private double a, b, c;
	private double cosAlpha;
	private double cosBeta;
	private double cosGamma;
	private double sinGamma;
	private double invSinGamma;
	private double unitVolume;
	public double volume;
	public double density;

	public Vector3 vecA = new Vector3();
	public Vector3 vecB = new Vector3();
	public Vector3 vecC = new Vector3();

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
		}transformToCartesian(new Vector3(1, 0, 0), vecA);

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

    public int getDegreesOfFreedom() {
        int degree = spaceGroup.getLinearDegreesOfFreedom() + spaceGroup.getAngularDegreesOfFreedom();
        for (WyckoffSite site : wyckoffSites) {
            degree += site.getDegreesOfFreedom();
        }

        return degree;
    }

	// Step Size = 0.01 in the wallpaper code....
	private static final double STEP_SIZE = 0.01;
	private int lastUpdateIdx;
	public void updateRandomVariable(double step_size_reduction_factor) {
		int idx = random.nextInt(basis.length);
		basis[idx].update(step_size_reduction_factor * STEP_SIZE);
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
		volume = a * b * c * unitVolume;
		density = numPositions / volume;

		transformToCartesian(new Vector3(1, 0, 0), vecA);
		transformToCartesian(new Vector3(0, 1, 0), vecB);
		transformToCartesian(new Vector3(0, 0, 1), vecC);

		// TSH added
		if (vecA.isUndefined()) {
			System.out.println("cartesian conversion failed: unitvolume = "+unitVolume+" , insqrt = " +  (1 - cosAlpha * cosAlpha - cosBeta * cosBeta - cosGamma * cosGamma + 2 * cosAlpha * cosBeta * cosGamma) + ", alpha = " +(spaceGroup.alpha*180/3.1415)+ ", beta = " +(spaceGroup.beta*180/3.1415)+ ", gamma = " +(spaceGroup.gamma*180/3.1415) );
		}

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

	public IsopointalSetResult saveResult() {
		IsopointalSetResult result = new IsopointalSetResult();
		result.numPositions = numPositions; //TSH
		result.numSites = wyckoffSites.size();
		result.isopointalSet = "" + spaceGroup.number;
		result.spaceGroup = spaceGroup.number;
		result.volume = volume;
		result.density = density;

		result.a = a;
		result.b = b;
		result.c = c;
		result.alpha = spaceGroup.alpha;
		result.beta = spaceGroup.beta;
		result.gamma = spaceGroup.gamma;

		result.highestMultInSpaceGroup = spaceGroup.getHighestMultiplicity();

		result.wyckoffPositions = new WyckoffPositionResult[numPositions];
		result.wyckoffSites = new WyckoffSiteResult[wyckoffSites.size()];

		int siteIdx = 0;
		int positionIdx = 0;
		int basisIdx = wyckoffStartIdx;
		for (WyckoffSite site : wyckoffSites) {
			result.isopointalSet += site.code;
			Vector3 posVariable = new Vector3();
			if (site.hasX)
				posVariable.x = basis[basisIdx++].curVal;
			if (site.hasY)
				posVariable.y = basis[basisIdx++].curVal;
			if (site.hasZ)
				posVariable.z = basis[basisIdx++].curVal;

			boolean first = true;

			for (WyckoffPosition position : site.positions) {
				WyckoffPositionResult positionResult = new WyckoffPositionResult();
				Vector3 pos = position.getPosition(posVariable);
				positionResult.code = site.code;
				positionResult.fracX = pos.x;
				positionResult.fracY = pos.y;
				positionResult.fracZ = pos.z;
				result.wyckoffPositions[positionIdx++] = positionResult;

				if (first) {
					WyckoffSiteResult siteResult = new WyckoffSiteResult();
					siteResult.code = site.code;
					siteResult.multiplicity = site.positions.size();

					siteResult.fracX = pos.x;
					siteResult.fracY = pos.y;
					siteResult.fracZ = pos.z;

					result.wyckoffSites[siteIdx++] = siteResult;
					first = false;
				}
			}
		}

		return result;
	}
}
