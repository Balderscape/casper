package au.com.clearboxsystems.casper.model;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 30/07/2015 1:38 PM
 */
public class Lattice {

	enum LatticeSystem {
		Triclinic,      // P only
		Monoclinic,     // P or C
		Orthorhombic,   // P, C, I or F
		Tetragonal,     // P or I
		Rhombohedral,   // P only (sometimes called R)
		Hexagonal,      // P only
		Cubic           // P, I or F
	}

	enum LatticeCentering {
		Primitive, // atoms in corners only, (P) and also (R) in the case of a Rhombohedral
		BodyCentered, // atoms in corners and center of unit cell (I)
		FaceCentered, // atoms in the corners and in the center of each face (F)
		BaseCentered  // atoms in the corners and in the center of two opposite faces (A), (B), or (C). Usually (C)
	}

}
