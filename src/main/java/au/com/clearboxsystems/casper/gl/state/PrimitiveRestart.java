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
public class PrimitiveRestart {
	
	public Boolean enabled = null;
	public Integer index;
	
	public void applyState(GL3 gl, PrimitiveRestartRequest request){
		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			if (enabled) {
				gl.glEnable(GL3.GL_PRIMITIVE_RESTART);
			} else {
				gl.glDisable(GL3.GL_PRIMITIVE_RESTART);
			}
			if (enabled) {
				if (index == null || index != request.index) {
					index = request.index;
					gl.glPrimitiveRestartIndex(index);
				}			
			}
		}
	}
}
