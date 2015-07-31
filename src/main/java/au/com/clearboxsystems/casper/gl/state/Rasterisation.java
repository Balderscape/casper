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
public class Rasterisation {

	public enum RasterisationMode {
		Point(GL3.GL_POINT),
		Line(GL3.GL_LINE),
		Fill(GL3.GL_FILL);
		public final int glType;

		private RasterisationMode(int glType) {
			this.glType = glType;
		}
	}
	
	private RasterisationMode rasterisationMode = null;
	
	public void applyState(GL3 gl, RasterisationRequest request) {
		if (rasterisationMode != request.rasterisationMode) {
			rasterisationMode = request.rasterisationMode;
			gl.glPolygonMode(GL3.GL_FRONT_AND_BACK, rasterisationMode.glType);
		}
	}

}
