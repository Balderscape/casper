package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.state.Blending.BlendingEquation;
import au.com.clearboxsystems.casper.gl.state.Blending.DestinationBlendingFactor;
import au.com.clearboxsystems.casper.gl.state.Blending.SourceBlendingFactor;

import java.awt.*;

/**
 *
 * @author Clearbox
 */
public class BlendingRequest {

	public boolean enabled = false;
	/** Specifies how the RGB source blending factors are computed. Default is ONE*/
	public SourceBlendingFactor sourceRGBFactor = SourceBlendingFactor.One;
	/** Specifies how the Alpha source blending factors are computed. Default is ONE*/
	public SourceBlendingFactor sourceAlphaFactor = SourceBlendingFactor.One;
	/** Specifies how the RGB destination blending factors are computed. Default is ZERO*/
	public DestinationBlendingFactor destinationRGBFactor = DestinationBlendingFactor.Zero;
	/** Specifies how the Alpha destination blending factors are computed. Default is ZERO*/
	public DestinationBlendingFactor destinationAlphaFactor = DestinationBlendingFactor.Zero;
	/** Specifies the RGB blending equation. Default is ADD*/
	public BlendingEquation RGBEquation = BlendingEquation.Add;
	/** Specifies the Alpha blending equation. Default is ADD*/
	public BlendingEquation alphaEquation = BlendingEquation.Add;
	public Color color = Color.BLACK;
	
}
