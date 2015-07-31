package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import com.jogamp.opengl.GL3;

import java.awt.*;

/**
 *
 * @author Clearbox
 */
public class ScissorTest {

	private Boolean enabled = null;
	private Rectangle rectangle = null;

	public void applyState(GL3 gl, ScissorTestRequest request) {

		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			if (enabled) {
				gl.glEnable(GL3.GL_SCISSOR_TEST);
			} else {
				gl.glDisable(GL3.GL_SCISSOR_TEST);
			}
		}

		if (enabled) {
			if (rectangle != request.rectangle) {
				rectangle = request.rectangle;
				gl.glScissor(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			}
		}

	}
}
