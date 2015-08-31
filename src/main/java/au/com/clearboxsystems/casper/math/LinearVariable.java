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

	public LinearVariable(Random random) {
		super(random);
		minVal = 0;
		maxVal = 1000; // No more than 1.4 * num atoms

		range = maxVal - minVal;
		curVal = minVal + (random.nextDouble() * range);
		lastVal = curVal;
	}

	@Override
	public void update(double stepSize) {
		lastVal = curVal;

		double nextVal;
		do {
			nextVal = curVal + (random.nextDouble() - 0.5) * range * stepSize;
		} while (nextVal < minVal || nextVal > maxVal);

		curVal = nextVal;
	}
}
