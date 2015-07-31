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
public class FacetCulling {
	
	private Boolean enabled = null;
	private CullFace cullFace = null;
	private WindingOrder windingOrder = null;

	public enum CullFace {
		/** Prevents rendering of the front facets of the geometry */
		Front(GL3.GL_FRONT),
		/** DEFAULT - Prevents rendering of the back facets of the geometry */
		Back(GL3.GL_BACK),
		/** In this mode no facets will be visible, but lines and points may be */
		FrontAndBack(GL3.GL_FRONT_AND_BACK);

		public final int glType;

		private CullFace(int glType) {
			this.glType = glType;
		}	
	}
	
	public enum WindingOrder {
		/** DEFAULT - Front faces are defined by a counter-clockwise winding order of vertices */
		CounterClockwise(GL3.GL_CCW),
		/** Front faces are defined by a clockwise winding order of vertices */
		Clockwise(GL3.GL_CW);

		public final int glType;
		
		private WindingOrder(int glType) {
			this.glType = glType;
		}	
	}

	public void applyState(GL3 gl, FacetCullingRequest request) {
		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			if (enabled)
				gl.glEnable(GL3.GL_CULL_FACE);
			else
				gl.glDisable(GL3.GL_CULL_FACE);				
		}
		
		if (enabled) {
			if (cullFace != request.cullFace) {
				cullFace = request.cullFace;
				gl.glCullFace(cullFace.glType);
			}
			if (windingOrder != request.windingOrder) {
				windingOrder = request.windingOrder;
				gl.glFrontFace(windingOrder.glType);
			}
		}
	}	
}
