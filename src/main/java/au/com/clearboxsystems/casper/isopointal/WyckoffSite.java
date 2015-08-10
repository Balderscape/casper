package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pauls
 * Timestamp: 10/08/2015 11:11 AM
 */
public class WyckoffSite {
	public final String code;
	public final int multiplicity;
	public final List<Vector3> positions;



	public WyckoffSite(String code, Vector3... positions) {
		this.code = code;

		this.positions = new ArrayList<>(positions.length);
		for (Vector3 position : positions)
			this.positions.add(position);

		multiplicity = positions.length;
	}

}
