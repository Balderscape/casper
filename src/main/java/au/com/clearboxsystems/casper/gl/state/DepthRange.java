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
public class DepthRange {
	private Double near;
	private Double far;
	
	public void applyState(GL3 gl, DepthRangeRequest request){
		if(near == null || near != request.near || far == null ||  far != request.far){
			near = request.near;
			far = request.far;
			gl.glDepthRange(near, far);
		}
	}

	public double getNear() {
		if (near == null)
			return 0;
		
		return near;
	}

	public double getFar() {
		if (far == null)
			return 1;

		return far;
	}
}
