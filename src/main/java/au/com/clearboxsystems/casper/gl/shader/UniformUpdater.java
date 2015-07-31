package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shape.Shape;

/**
 * User: pauls
 * Timestamp: 6/01/14 3:35 PM
 */
public interface UniformUpdater<T> {
	public T computeUniformValue(Shape shape);
	public void updateUniformValue(Uniform uniform, T value);
}
