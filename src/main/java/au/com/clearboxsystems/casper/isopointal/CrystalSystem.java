package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 11/08/15.
 */
public enum CrystalSystem {
	Triclinic,
	Monoclinic,
	Orthorhombic,
	Tetragonal,
	Trigonal,
	Hexagonal,
	Cubic;

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
