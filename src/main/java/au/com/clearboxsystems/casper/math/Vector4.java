package au.com.clearboxsystems.casper.math;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.nio.ByteBuffer;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:04 PM
 */
public class Vector4 {
	public double x, y, z, w;

	public Vector4() {
	}

	public Vector4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void toByteBufferAsFloat(ByteBuffer buffer, int offset) {
		buffer.putFloat(offset, (float)x);
		buffer.putFloat(offset + 4, (float)y);
		buffer.putFloat(offset + 8, (float)z);
		buffer.putFloat(offset + 12, (float)w);
	}

}
