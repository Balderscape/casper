package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.awt.*;

/**
 *
 * @author Clearbox
 */
public class ScissorTestRequest {

	public boolean enabled = false;
	/**Specifies the scissor rectangle*/
	public Rectangle rectangle = new Rectangle(0, 0, 0, 0);
}
