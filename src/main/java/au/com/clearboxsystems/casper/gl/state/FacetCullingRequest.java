package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.state.FacetCulling.CullFace;
import au.com.clearboxsystems.casper.gl.state.FacetCulling.WindingOrder;

/**
 *
 * @author Paul Solomon
 */
public class FacetCullingRequest {

	public boolean enabled = true;
	/** This is the face that will be culled by the GPU, default is Back */
	public CullFace cullFace = CullFace.Back;
	/** This is the winding order the defines what the Front Facet is, default is CCW */
	public WindingOrder windingOrder = WindingOrder.CounterClockwise;
}
