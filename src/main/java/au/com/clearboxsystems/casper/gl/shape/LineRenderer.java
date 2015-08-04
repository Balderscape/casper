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
 * Timestamp: 29/01/14 3:28 PM
 */
public class LineRenderer extends Shape  {
	private static final String VERTEX_SHADER_SRC = "/au/com/clearboxsystems/casper/gl/shader/line.vert.glsl";
	private static final String GEOMETRY_SHADER_SRC = "/au/com/clearboxsystems/casper/gl/shader/line.geom.glsl";
	private static final String FRAGMENT_SHADER_SRC = "/au/com/clearboxsystems/casper/gl/shader/line.frag.glsl";

	private List<Line> lines = new ArrayList<>();

	private Shader shader;
	private VertexBuffer startStopPositionBuffer;
	private VertexArray vertexArray;
	private Uniform u_shininess;
	private Uniform u_lineRadius;

	public LineRenderer(Scene scene) {
		super(scene);

		shader = new Shader();
		startStopPositionBuffer = new VertexBuffer(BufferType.DynamicDraw, 24);

		vertexArray = new VertexArray();
		vertexArray.attachVertexBuffer(startStopPositionBuffer, "startPosition", 3, 24, 0, GL3.GL_FLOAT);
		vertexArray.attachVertexBuffer(startStopPositionBuffer, "stopPosition", 3, 24, 12, GL3.GL_FLOAT);
	}

	public void addLine(Line line) {
		lines.add(line);
	}

	public void clear() {
		lines.clear();
	}

	@Override
	public void init(GL3 gl) {
		startStopPositionBuffer.initalise(gl);
		vertexArray.initalise(gl);

		shader.initialise(gl);
		shader.compileAndLinkShaderProgramFromResource(gl, VERTEX_SHADER_SRC, GEOMETRY_SHADER_SRC, FRAGMENT_SHADER_SRC);

		u_shininess = shader.getUniform("specularShininess");
		u_shininess.setValue(0.1);

		u_lineRadius = shader.getUniform("lineRadius");
		u_lineRadius.setValue(0.1);

		gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE);
	}

	public void update() {
		startStopPositionBuffer.startLoading(24 * lines.size());
		for (Line line : lines) {
			startStopPositionBuffer.add(line.startPosition);
			startStopPositionBuffer.add(line.stopPosition);
		}
		startStopPositionBuffer.finishLoading();
	}

	@Override
	public void render(GL3 gl, RenderState renderState) {
		shader.bind(gl);
		shader.clean(gl, this);

		vertexArray.bind(gl);
		vertexArray.clean(gl, shader);

		gl.glDrawArrays(
				GL3.GL_POINTS, // Mode
				0, // Start
				startStopPositionBuffer.getCount()); // Count
	}
}
