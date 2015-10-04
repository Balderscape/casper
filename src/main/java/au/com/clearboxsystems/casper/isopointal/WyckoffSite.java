package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pauls
 * Timestamp: 10/08/2015 11:11 AM
 */
public class WyckoffSite {
	public String code;
	public String positionsShortForm;
	public List<WyckoffPosition> positions = new ArrayList<>();

	@JsonIgnore
	public boolean hasX = false;
	@JsonIgnore
	public boolean hasY = false;
	@JsonIgnore
	public boolean hasZ = false;

	@JsonIgnore
	boolean init = false;

	private void initDegreesOfFreedom() {
		for (WyckoffPosition position : positions) {
			if (!hasX && (position.xTransform.xScale != 0 ||position.yTransform.xScale != 0 || position.zTransform.xScale != 0))
				hasX = true;
			if (!hasY && (position.xTransform.yScale != 0 ||position.yTransform.yScale != 0 || position.zTransform.yScale != 0))
				hasY = true;
			if (!hasZ && (position.xTransform.zScale != 0 ||position.yTransform.zScale != 0 || position.zTransform.zScale != 0))
				hasZ = true;
		}

		init = true;
	}

	@JsonIgnore
	public int getDegreesOfFreedom() {
		if (!init)
			initDegreesOfFreedom();

		return (hasX ? 1 : 0) + (hasY ? 1 : 0) + (hasZ ? 1 : 0);
	}
}
