package au.com.clearboxsystems.casper.gl.shape;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.buffer.BufferType;
import au.com.clearboxsystems.casper.gl.buffer.VertexArray;
import au.com.clearboxsystems.casper.gl.buffer.VertexBuffer;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.Shader;
import au.com.clearboxsystems.casper.gl.shader.Uniform;
import au.com.clearboxsystems.casper.gl.state.RenderState;
import com.jogamp.opengl.GL3;

import java.util.ArrayList;
import java.util.List;

/**
 * User: pauls
 * Timestamp: 13/01/14 2:46 PM
 */
public class SphereRenderer extends Shape {

	private static final String VERTEX_SHADER_SRC = "/au/com/clearboxsystems/casper/gl/shader/sphere.imposter.vert.glsl";
	private static final String GEOMETRY_SHADER_SRC = "/au/com/clearboxsystems/casper/gl/shader/sphere.imposter.geom.glsl";
	private static final String FRAGMENT_SHADER_SRC = "/au/com/clearboxsystems/casper/gl/shader/sphere.imposter.frag.glsl";

	private List<Sphere> spheres = new ArrayList<>();
	private boolean atomListChanged = false;

	private Shader shader;
	private VertexBuffer atomPositionBuffer;
	private VertexBuffer atomRadiusMaterialBuffer;
	private VertexArray vertexArray;
	private Uniform u_shininess;
	private Uniform u_percentRadius;
	private boolean isSpaceFilling = false;
	private boolean spaceFillingChanged = true;

	public SphereRenderer(Scene scene) {
		super(scene, null);
		shader = new Shader();
		atomPositionBuffer = new VertexBuffer(BufferType.DynamicDraw, 12);
		atomRadiusMaterialBuffer = new VertexBuffer(BufferType.StaticDraw, 20);

		vertexArray = new VertexArray();
		vertexArray.attachVertexBuffer(atomPositionBuffer, "position", 3);
		vertexArray.attachVertexBuffer(atomRadiusMaterialBuffer, "radius", 1, 20, 0, GL3.GL_FLOAT);
		vertexArray.attachVertexBuffer(atomRadiusMaterialBuffer, "material", 4, 20, 4, GL3.GL_FLOAT);
	}

	public void addSphere(Sphere sphere) {
		spheres.add(sphere);
		atomListChanged = true;
	}

	public void clear() {
		spheres.clear();
		atomListChanged = true;
	}

	@Override
	public void init(GL3 gl) {

		atomPositionBuffer.initalise(gl);
		atomRadiusMaterialBuffer.initalise(gl);
		vertexArray.initalise(gl);

		shader.initialise(gl);
		shader.compileAndLinkShaderProgramFromResource(gl, VERTEX_SHADER_SRC, GEOMETRY_SHADER_SRC, FRAGMENT_SHADER_SRC);

		u_shininess = shader.getUniform("specularShininess");
		u_shininess.setValue(0.1);

		u_percentRadius = shader.getUniform("percentRadius");

		gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE);
	}

    public void setDirty() {
        atomListChanged = true;
    }

	public boolean isSpaceFilling() {
		return isSpaceFilling;
	}

	public void setSpaceFilling(boolean spaceFilling) {
		isSpaceFilling = spaceFilling;
		spaceFillingChanged = true;
	}

	public void update() {
		atomPositionBuffer.startLoading(12 * spheres.size());
		if (atomListChanged)
			atomRadiusMaterialBuffer.startLoading(20 * spheres.size());
		for (Sphere sphere : spheres) {
			atomPositionBuffer.add(sphere.position);
			if (atomListChanged) {
				atomRadiusMaterialBuffer.add((float) sphere.radius);
				atomRadiusMaterialBuffer.add(sphere.material);
			}
		}
		atomPositionBuffer.finishLoading();
		if (atomListChanged)
			atomRadiusMaterialBuffer.finishLoading();
		atomListChanged = false;
	}

	@Override
	public void render(GL3 gl, RenderState renderState) {
		if (spaceFillingChanged) {
			u_percentRadius.setValue(isSpaceFilling ? 1.0 : 0.28);
		}

		shader.bind(gl);
		shader.clean(gl, this);

		vertexArray.bind(gl);
		vertexArray.clean(gl, shader);

		gl.glDrawArrays(
				GL3.GL_POINTS, // Mode
				0, // Start
				atomPositionBuffer.getCount()); // Count
	}
}
