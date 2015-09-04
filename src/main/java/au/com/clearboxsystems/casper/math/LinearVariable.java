package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.util.Random;

/**
 * User: pauls
 * Timestamp: 25/08/2015 10:18 AM
 */
public class LinearVariable extends Variable {

	// FIXME: We probably need a way to initialise this to a sensible unit cell size

	public LinearVariable(Random random) {
		super(random);
		minVal = 0;
		maxVal = 20; // No more than 1.4 * num atoms

		range = maxVal - minVal;
		curVal = minVal + (random.nextDouble() * range);
		lastVal = curVal;
	}

	@Override
	public void update(double stepSize) {
		lastVal = curVal;

		curVal *= (1 + (random.nextDouble() - 0.5) * stepSize); // Still has a bias, fix with logs...
	}
}
