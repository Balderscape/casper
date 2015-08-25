package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.util.Random;

/**
 * User: pauls
 * Timestamp: 25/08/2015 10:05 AM
 */
public abstract class Variable {

	public final Random random;

	public double curVal;
	public double lastVal;

	public double minVal;
	public double maxVal;
	public double range;

	public Variable(Random random) {
		this.random = random;
	}

	public abstract void update(double stepSize);

	public void revert() {
		curVal = lastVal;
	}
}
