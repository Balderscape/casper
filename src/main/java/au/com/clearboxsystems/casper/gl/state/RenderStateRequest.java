package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

/**
 *
 * @author Paul Solomon
 */
public class RenderStateRequest {

	public PrimitiveRestartRequest primitiveRestart;
	public FacetCullingRequest facetCulling;
//	public ProgramPointSize programPointSize;
	public RasterisationRequest rasterisation;
	public ScissorTestRequest scissorTest;
	public StencilTestRequest stencilTest;
	public DepthTestRequest depthTest;
	public DepthRangeRequest depthRange;
	public BlendingRequest blending;
	public ColorMaskRequest colorMask;
	public DepthMaskRequest depthMask;

	
	/**
	 * Create a new RenderStateRequest, if initialise, all renderState request objects
	 * will be initialise and set to their default values, if not initialised all
	 * request objects will be set to null, which means ignore.
	 * 
	 * @param initialise
	 */
	public RenderStateRequest(boolean initialise) {
		if (initialise) {
			primitiveRestart = new PrimitiveRestartRequest();
			facetCulling = new FacetCullingRequest();
	//		programPointSize = ProgramPointSize.Disabled;
			rasterisation = new RasterisationRequest();
			scissorTest = new ScissorTestRequest();
			stencilTest = new StencilTestRequest();
			depthTest = new DepthTestRequest();
			depthRange = new DepthRangeRequest();
			blending = new BlendingRequest();
			colorMask = new ColorMaskRequest();
			depthMask = new DepthMaskRequest();		
		}
	}

	public RenderStateRequest combine(RenderStateRequest _renderState) {
		RenderStateRequest res = new RenderStateRequest(false);
		if (_renderState.primitiveRestart != null)
			res.primitiveRestart = _renderState.primitiveRestart;
		else
			res.primitiveRestart = primitiveRestart;

		if (_renderState.facetCulling != null)
			res.facetCulling = _renderState.facetCulling;
		else
			res.facetCulling = facetCulling;

		if (_renderState.rasterisation != null)
			res.rasterisation = _renderState.rasterisation;
		else
			res.rasterisation = rasterisation;

		if (_renderState.scissorTest != null)
			res.scissorTest = _renderState.scissorTest;
		else
			res.scissorTest = scissorTest;

		if (_renderState.stencilTest != null)
			res.stencilTest = _renderState.stencilTest;
		else
			res.stencilTest = stencilTest;

		if (_renderState.depthTest != null)
			res.depthTest = _renderState.depthTest;
		else
			res.depthTest = depthTest;

		if (_renderState.depthRange != null)
			res.depthRange = _renderState.depthRange;
		else
			res.depthRange = depthRange;

		if (_renderState.blending != null)
			res.blending = _renderState.blending;
		else
			res.blending = blending;

		if (_renderState.colorMask != null)
			res.colorMask = _renderState.colorMask;
		else
			res.colorMask = colorMask;

		if (_renderState.depthMask != null)
			res.depthMask = _renderState.depthMask;
		else
			res.depthMask = depthMask;

		return res;		
	}
	
	
}
