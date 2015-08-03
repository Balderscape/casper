package au.com.clearboxsystems.casper.md;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 * User: pauls
 * Timestamp: 3/08/2015 3:34 PM
 */
public class StatisticalDouble {

	private double sum = 0;
	private double sum2 = 0;
	private int count = 0;

	public void zero() {
		sum = 0;
		sum2 = 0;
		count = 0;
	}

	public void accum(double val) {
		sum += val;
		sum2 += val * val;
		count++;
	}

	public double average() {
		return sum / count;
	}

	public double stdDev() {
		return Math.sqrt(Math.max(0, sum2 / count - (sum/count * sum/count)));
	}
}
