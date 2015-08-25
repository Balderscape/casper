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

		curVal = random.nextDouble(); // FIXME: This may need to be a log or something...
		lastVal = curVal;
	}

	public void update(double stepSize) {
		lastVal = curVal;
		curVal *= 1 + ((random.nextDouble() - 0.5) * stepSize); // FIXME: This seems to be lob sided, easier to approach one...

		while (curVal > 1)
			curVal -= 1;

	}
}
