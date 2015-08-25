package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 11/08/15.
 */
public enum CrystalSystem {
	Triclinic(3,3),
	Monoclinic(3, 1),
	Orthorhombic(3,0),
	Tetragonal(2,0),
	Trigonal(2, 1), // This may be wrong... not sure how rhombohedral/hexogonal works...
	Hexagonal(2, 0),
	Cubic(1, 0);

	public final int linearDegreesOfFreedom;
	public final int angularDegreesOfFreedom;

	CrystalSystem(int linearDegreesOfFreedom, int angularDegreesOfFreedom) {
		this.linearDegreesOfFreedom = linearDegreesOfFreedom;
		this.angularDegreesOfFreedom = angularDegreesOfFreedom;
	}

	public int getDegreesOfFreedom() {
		return linearDegreesOfFreedom + angularDegreesOfFreedom;
	}

	public int getIdxA() {
		return 0;
	}
	public int getIdxB() {
		if (linearDegreesOfFreedom == 3)
			return 1;

		return 0;
	}

	public int getIdxC() {
		return linearDegreesOfFreedom - 1;
	}

	public int getIdxAlpha() {
		if (angularDegreesOfFreedom == 3)
			return linearDegreesOfFreedom;

		return -1;
	}

	public int getIdxBeta() {
		if (angularDegreesOfFreedom == 3)
			return linearDegreesOfFreedom + 1;

		return -1;
	}

	public int getIdxGamma() {
		if (angularDegreesOfFreedom == 0)
			return -1;

		return linearDegreesOfFreedom + angularDegreesOfFreedom - 1;
	}

	public static CrystalSystem fromSpaceGroup(int number) {
		if (number <= 2)
			return Triclinic;
		if (number <= 15)
			return Monoclinic;
		if (number <= 74)
			return Orthorhombic;
		if (number <= 142)
			return Tetragonal;
		if (number <= 167)
			return Trigonal;
		if (number <= 194)
			return Hexagonal;

		return Cubic;
	}
}
