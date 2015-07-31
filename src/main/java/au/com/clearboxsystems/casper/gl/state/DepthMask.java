package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import com.jogamp.opengl.GL3;

/**
 *
 * @author Paul Solomon
 */
public class DepthMask {
	
	private Boolean enabled = null;
	
	public void applyState(GL3 gl, DepthMaskRequest request) {
		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			gl.glDepthMask(enabled);
		}
	}
}
