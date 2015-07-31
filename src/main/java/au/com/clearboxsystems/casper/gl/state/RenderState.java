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
public class RenderState {

	private PrimitiveRestart primitiveRestart;
	private FacetCulling facetCulling;
//	private ProgramPointSize programPointSize;
	private Rasterisation rasterisation;
	private ScissorTest scissorTest;
	private StencilTest stencilTest;
	private DepthTest depthTest;
	private DepthRange depthRange;
	private Blending blending;
	private ColorMask colorMask;
	private DepthMask depthMask;

	public RenderState() {
		primitiveRestart = new PrimitiveRestart();
		facetCulling = new FacetCulling();
//		programPointSize = ProgramPointSize.Disabled;
		rasterisation = new Rasterisation();
		scissorTest = new ScissorTest();
		stencilTest = new StencilTest();
		depthTest = new DepthTest();
		depthRange = new DepthRange();
		blending = new Blending();
		colorMask = new ColorMask();
		depthMask = new DepthMask();		
	}
	
	public void applyRenderStateRequest(GL3 gl, RenderStateRequest request) {
		if (request.primitiveRestart != null)
			primitiveRestart.applyState(gl, request.primitiveRestart);
		if (request.facetCulling != null)
			facetCulling.applyState(gl, request.facetCulling);
		if (request.rasterisation != null)
			rasterisation.applyState(gl, request.rasterisation);
		if (request.scissorTest != null)
			scissorTest.applyState(gl, request.scissorTest);
		if (request.stencilTest != null)
			stencilTest.applyState(gl, request.stencilTest);
		if (request.depthTest != null)
			depthTest.applyState(gl, request.depthTest);
		if (request.depthRange != null)
			depthRange.applyState(gl, request.depthRange);
		if (request.blending != null)
			blending.applyState(gl, request.blending);
		if (request.colorMask != null)
			colorMask.applyState(gl, request.colorMask);
		if (request.depthMask != null)
			depthMask.applyState(gl, request.depthMask);
	}

	public double getDepthRangeNear() {
		return depthRange.getNear();
	}

	public double getDepthRangeFar() {
		return depthRange.getFar();
	}
	
}
