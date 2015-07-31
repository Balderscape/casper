package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import com.jogamp.opengl.GL3;

/**
 *
 * @author Clearbox
 */
public class StencilTest {

	private Boolean enabled = null;
	private StencilTestFace frontFace;
	private StencilTestFace backFace;

	public void applyState(GL3 gl, StencilTestRequest request) {
		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			if (enabled) {
				gl.glEnable(GL3.GL_STENCIL_TEST);
			} else {
				gl.glDisable(GL3.GL_STENCIL_TEST);
			}
		}

		if (enabled) {
			frontFace.applyState(gl, GL3.GL_FRONT, request.frontFace);
			backFace.applyState(gl, GL3.GL_BACK, request.backFace);
		}

	}
}
