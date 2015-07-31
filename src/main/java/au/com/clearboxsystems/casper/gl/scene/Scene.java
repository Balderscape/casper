package au.com.clearboxsystems.casper.gl.scene;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import au.com.clearboxsystems.casper.gl.buffer.VertexArray;
import au.com.clearboxsystems.casper.gl.shader.DynamicUniformManager;
import au.com.clearboxsystems.casper.gl.shader.Shader;
import au.com.clearboxsystems.casper.gl.shader.block.Light;
import au.com.clearboxsystems.casper.gl.state.Blending;
import au.com.clearboxsystems.casper.gl.state.RenderState;
import au.com.clearboxsystems.casper.gl.state.RenderStateRequest;
import au.com.clearboxsystems.casper.math.Matrix4;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:41 PM
 */
public class Scene implements GLEventListener {

	RenderState renderState = new RenderState();

	private Camera camera;
	private Light light;
	private Color backgroundColour;

	private ArrayList<Renderable> renderables = new ArrayList<Renderable>();
	private final ArrayList<Renderable> uninitalisedRenderables = new ArrayList<Renderable>();

	private RenderStateRequest renderStateRequest;

	public Scene(Camera camera) {
		this.camera = camera;
		camera.setScene(this);

		this.backgroundColour = new Color(3, 3, 3);

		renderStateRequest = new RenderStateRequest(true);
		renderStateRequest.facetCulling.enabled = true;
		renderStateRequest.depthTest.enabled = true;
		renderStateRequest.blending.enabled = true;
		renderStateRequest.blending.sourceAlphaFactor = Blending.SourceBlendingFactor.OneMinusSourceAlpha;

	}

	public Light getLight() {
		return light;
	}

	public void setLight(Light light) {
		this.light = light;
		DynamicUniformManager.dynamicUniformBlocks.put("Light", light);
	}

	public Camera getCamera() {
		return camera;
	}

	public void setBackgroundColor(int red, int green, int blue) {
		backgroundColour = new Color(red, green, blue);
	}

	public void addRenderable(Renderable renderable) {
		synchronized(uninitalisedRenderables) {
			uninitalisedRenderables.add(renderable);
		}
	}

	public void init(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
//		gl.glEnable(GL.GL_LINE_SMOOTH);
//		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
//		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
//		gl.glEnable(GL2.GL_MULTISAMPLE);
//		gl.glEnable(GL2.GL_SAMPLE_BUFFERS);

		renderState.applyRenderStateRequest(gl, renderStateRequest);

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();

		if (!uninitalisedRenderables.isEmpty()) {
			synchronized (uninitalisedRenderables) {
				ListIterator<Renderable> initList = uninitalisedRenderables.listIterator();
				while (initList.hasNext()) {
					Renderable r = initList.next();
					r.init(gl);
					renderables.add(r);
					initList.remove();
				}
			}
		}
		DynamicUniformManager.updateDynamicUniformBlocks(this);

		gl.glClearColor(backgroundColour.getRed() / 255f, backgroundColour.getGreen() / 255f, backgroundColour.getBlue() / 255f, 1);
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

		DynamicUniformManager.cleanDynamicUniformBlocks(gl);
		for (Renderable r : renderables) {
			r.render(gl, renderState);
		}

		VertexArray.unbind(gl);
		Shader.unbind(gl);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		camera.setViewport(x, y, width, height);
	}

	public double getDepthRangeNear() {
		return renderState.getDepthRangeNear();
	}

	public double getDepthRangeFar() {
		return renderState.getDepthRangeFar();
	}

	public Matrix4 getViewPerspective() {
		return camera.getViewPerspectiveMatrix();
	}

	public RenderStateRequest getRenderStateRequest() {
		return renderStateRequest;
	}
}
