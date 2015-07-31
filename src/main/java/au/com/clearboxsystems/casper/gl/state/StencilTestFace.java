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
public class StencilTestFace {

	private StencilOperation stencilFailOperation = null;
	private StencilOperation depthFailStencilOperation = null;
	private StencilOperation depthPassStencilOperation = null;
	private StencilTestFunction function = null;
	private int referenceValue;
	private int mask;

	public enum StencilOperation {

		/** Sets stencil buffer value to 0*/
		Zero(GL3.GL_ZERO),
		/** Inverts bitwise, the stencil buffer value*/
		Invert(GL3.GL_INVERT),
		/** Keeps current stencil buffer value*/
		Keep(GL3.GL_KEEP),
		Replace(GL3.GL_REPLACE),
		/**Increments stencil buffer value*/
		Increment(GL3.GL_INCR),
		/** Decrements stencil buffer value*/
		Decrement(GL3.GL_DECR),
		/** Increments stencil buffer value. Wraps value to zero when incrementing the maximum possible unsigned value*/
		IncrementWrap(GL3.GL_INCR_WRAP),
		/** Decrements stencil buffer value. Wraps value to largest possible unsigned value when decrementing a value of zero*/
		DecrementWrap(GL3.GL_DECR_WRAP);
		public final int glType;

		private StencilOperation(int glType) {
			this.glType = glType;
		}
	}

	public enum StencilTestFunction {

		/** Stencil Function always fails*/
		Never(GL3.GL_NEVER),
		/** Stencil Function passes if (ref & mask) < (stencil & mask)*/
		Less(GL3.GL_LESS),
		/** Stencil Function passes if (ref & mask) = (stencil & mask)*/
		Equal(GL3.GL_EQUAL),
		/** Stencil Function passes if (ref & mask) <= (stencil & mask)*/
		LessThanOrEqual(GL3.GL_LEQUAL),
		/** Stencil Function passes if (ref & mask) > (stencil & mask)*/
		Greater(GL3.GL_GREATER),
		/** Stencil Function passes if (ref & mask) != (stencil & mask)*/
		NotEqual(GL3.GL_NOTEQUAL),
		/** Stencil Function passes if (ref & mask) >= (stencil & mask)*/
		GreaterThanOrEqual(GL3.GL_GEQUAL),
		/** Stencil Function always passes*/
		Always(GL3.GL_ALWAYS);
		public final int glType;

		private StencilTestFunction(int glType) {
			this.glType = glType;
		}
	}

	public void applyState(GL3 gl, int face, StencilTestFace request) {

		if (stencilFailOperation != request.stencilFailOperation
				|| depthFailStencilOperation != request.depthFailStencilOperation
				|| depthPassStencilOperation != request.depthPassStencilOperation) {

			stencilFailOperation = request.stencilFailOperation;
			depthFailStencilOperation = request.depthFailStencilOperation;
			depthPassStencilOperation = request.depthPassStencilOperation;


			gl.glStencilOpSeparate(
					face,
					stencilFailOperation.glType,
					depthFailStencilOperation.glType,
					depthPassStencilOperation.glType);

		}

		if (function != request.function || referenceValue != request.referenceValue || mask != request.mask) {

			function = request.function;
			referenceValue = request.referenceValue;
			mask = request.mask;

			gl.glStencilFuncSeparate(
					face,
					function.glType,
					referenceValue,
					mask);
		}
	}
}
