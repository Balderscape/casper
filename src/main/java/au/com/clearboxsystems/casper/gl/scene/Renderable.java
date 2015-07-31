package au.com.clearboxsystems.casper.gl.scene;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import au.com.clearboxsystems.casper.gl.state.RenderState;
import com.jogamp.opengl.GL3;

/**
 * User: pauls
 * Timestamp: 6/01/14 3:09 PM
 */
public interface Renderable {

	void init(GL3 gl);
	void render(GL3 gl, RenderState renderState);
}
