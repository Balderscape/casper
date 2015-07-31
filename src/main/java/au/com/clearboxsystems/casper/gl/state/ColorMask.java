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
public class ColorMask {
	
	private Boolean red = null;
	private Boolean green = null;
	private Boolean blue = null;
	private Boolean alpha = null;

	public void applyState(GL3 gl, ColorMaskRequest request){
		
		if (red == null || red != request.red || green == null || green != request.green ||
				blue == null || blue != request.blue || alpha == null || alpha != request.alpha){
			red = request.red;
			green = request.green;
			blue = request.blue;
			alpha = request.alpha;

			gl.glColorMask(red, green, blue, alpha);
		}

	}
	
}
