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
public class DepthTest {

	private Boolean enabled = null;
	private DepthTestFunction depthTestFunction = null;

	public enum DepthTestFunction {

		/** Function never passes*/
		Never(GL3.GL_NEVER),
		/** DEFAULT - Passes if the depth value is less than the value in the depth buffer*/
		Less(GL3.GL_LESS),
		/** Passes if the depth value is equal to the value in the depth buffer*/
		Equal(GL3.GL_EQUAL),
		/** Passes if the depth value is less than or equal to the value in the depth buffer*/
		LessThanOrEqual(GL3.GL_LEQUAL),
		/** Passes if the depth value is greater than the value in the depth buffer*/
		Greater(GL3.GL_GREATER),
		/** Passes if the depth value is not equal to the value in the depth buffer*/
		NotEqual(GL3.GL_NOTEQUAL),
		/** Passes if the depth value is greater than or equal to the value in the depth buffer*/
		GreaterThanOrEqual(GL3.GL_GEQUAL),
		/** Function always passes*/
		Always(GL3.GL_ALWAYS);
		public final int glType;

		private DepthTestFunction(int glType) {
			this.glType = glType;
		}
	}

	public void applyState(GL3 gl, DepthTestRequest request) {
		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			if (enabled) {
				gl.glEnable(GL3.GL_DEPTH_TEST);
			} else {
				gl.glDisable(GL3.GL_DEPTH_TEST);
			}
		}
		if (enabled) {
			if (depthTestFunction != request.depthTestFunction) {
				depthTestFunction = request.depthTestFunction;
				gl.glDepthFunc(depthTestFunction.glType);
			}
		}
	}
}
