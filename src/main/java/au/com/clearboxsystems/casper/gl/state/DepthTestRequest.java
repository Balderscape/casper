package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.state.DepthTest.DepthTestFunction;


/**
 *
 * @author Clearbox
 */
public class DepthTestRequest {
	
	public boolean enabled = true;
	/** This is the function used to compare pixel depth values. Default value is Less */
	public DepthTestFunction depthTestFunction = DepthTestFunction.Less;
	
}
