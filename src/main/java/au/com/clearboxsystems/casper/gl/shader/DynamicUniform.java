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
public class DynamicUniform<T> {
	private T value;
	private UniformUpdater<T> uniformUpdater;

	public DynamicUniform(UniformUpdater<T> uniformUpdater) {
		this.uniformUpdater = uniformUpdater;
	}

	public void setUniform(Shape shape, Uniform uniform) {
		value = uniformUpdater.computeUniformValue(shape);
		uniformUpdater.updateUniformValue(uniform, value);
	}
}
