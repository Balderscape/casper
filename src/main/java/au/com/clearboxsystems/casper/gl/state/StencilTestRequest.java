package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 *
 * @author Clearbox
 */
public class StencilTestRequest {

	public boolean enabled = false;
	public StencilTestFace frontFace;
	public StencilTestFace backFace;
}
