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
public class RelativeVariable extends Variable {

	public RelativeVariable(Random random) {
		super(random);

		curVal = random.nextDouble();
		lastVal = curVal;
	}

	public void update(double stepSize) {
		lastVal = curVal;
		curVal += (random.nextDouble() - 0.5) * stepSize;

		while (curVal > 1)
			curVal -= 1;
		while (curVal < 0)
			curVal += 1;
	}
}
