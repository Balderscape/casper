package au.com.clearboxsystems.casper.gl.shape;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.buffer.*;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.Shader;
import au.com.clearboxsystems.casper.gl.shader.Uniform;
import au.com.clearboxsystems.casper.gl.state.RenderState;
import au.com.clearboxsystems.casper.math.Vector2;
import au.com.clearboxsystems.casper.math.Vector3;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

import java.awt.*;

/**
 * User: pauls
 * Timestamp: 6/01/14 3:23 PM
 */
public class Cube extends Shape {

	private static final String vertShader = "/au/com/clearboxsystems/casper/gl/shader/phong.vert.glsl";
	private static final String fragShader = "/au/com/clearboxsystems/casper/gl/shader/phong.frag.glsl";

	private VertexBuffer vertexNormalBuffer;
	private VertexBuffer texCoord;
	private IndexBuffer indexBuffer;
	private VertexArray vertexArray;
	private Shader shader;
	private Uniform u_color;
	private Uniform u_textured;
	private Uniform u_texture0;
	private Color color = Color.WHITE;
	private Texture texture;
	private String textureResource;
	private int numVertices = 24;
	private int numFaces = 6;
	private boolean isTextured = false;

	double x, y, z;

	public Cube(Scene scene, Shape parent, double x, double y, double z) {
		super(scene, parent);
		this.x = x;
		this.y = y;
		this.z = z;

		vertexNormalBuffer = new VertexBuffer(BufferType.StaticDraw, 6 * Buffers.SIZEOF_FLOAT);
		vertexNormalBuffer.startLoading(6 * Buffers.SIZEOF_FLOAT * numVertices);
		buildVertexNormalBuffer();
		vertexNormalBuffer.finishLoading();

		indexBuffer = new IndexBuffer(3 * Buffers.SIZEOF_FLOAT * 2 * numFaces);
		buildIndices();
		texCoord = new VertexBuffer(BufferType.StaticDraw);
		texCoord.startLoading(2 * Buffers.SIZEOF_FLOAT * 24);
		buildTextCoord();
		texCoord.finishLoading();

		vertexArray = new VertexArray();
		vertexArray.attachIndexBuffer(indexBuffer);
		vertexArray.attachVertexBuffer(vertexNormalBuffer, "position", 3, 24, 0, GL3.GL_FLOAT);
		vertexArray.attachVertexBuffer(vertexNormalBuffer, "normal", 3, 24, 12, GL3.GL_FLOAT);
		vertexArray.attachVertexBuffer(texCoord, "texCoord", 2);

		shader = new Shader();

		texture = new Texture();
	}

	private void buildVertexNormalBuffer() {
		//top face
		vertexNormalBuffer.add(new Vector3(-x / 2, -y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, 1));
		vertexNormalBuffer.add(new Vector3(x / 2, -y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, 1));
		vertexNormalBuffer.add(new Vector3(x / 2, y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, 1));
		vertexNormalBuffer.add(new Vector3(-x / 2, y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, 1));

		//front face
		vertexNormalBuffer.add(new Vector3(-x / 2, -y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, -1, 0));
		vertexNormalBuffer.add(new Vector3(x / 2, -y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, -1, 0));
		vertexNormalBuffer.add(new Vector3(x / 2, -y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, -1, 0));
		vertexNormalBuffer.add(new Vector3(-x / 2, -y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, -1, 0));

		//right face
		vertexNormalBuffer.add(new Vector3(x / 2, -y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(1, 0, 0));
		vertexNormalBuffer.add(new Vector3(x / 2, y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(1, 0, 0));
		vertexNormalBuffer.add(new Vector3(x / 2, y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(1, 0, 0));
		vertexNormalBuffer.add(new Vector3(x / 2, -y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(1, 0, 0));

		//back face
		vertexNormalBuffer.add(new Vector3(x / 2, y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, 1, 0));
		vertexNormalBuffer.add(new Vector3(-x / 2, y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(0, 1, 0));
		vertexNormalBuffer.add(new Vector3(-x / 2, y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, 1, 0));
		vertexNormalBuffer.add(new Vector3(x / 2, y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, 1, 0));

		//left face
		vertexNormalBuffer.add(new Vector3(-x / 2, y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(-1, 0, 0));
		vertexNormalBuffer.add(new Vector3(-x / 2, -y / 2, z / 2));
		vertexNormalBuffer.add(new Vector3(-1, 0, 0));
		vertexNormalBuffer.add(new Vector3(-x / 2, -y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(-1, 0, 0));
		vertexNormalBuffer.add(new Vector3(-x / 2, y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(-1, 0, 0));

		//bottom face
		vertexNormalBuffer.add(new Vector3(-x / 2, -y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, -1));
		vertexNormalBuffer.add(new Vector3(x / 2, -y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, -1));
		vertexNormalBuffer.add(new Vector3(x / 2, y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, -1));
		vertexNormalBuffer.add(new Vector3(-x / 2, y / 2, -z / 2));
		vertexNormalBuffer.add(new Vector3(0, 0, -1));
	}

	private void buildIndices() {
		//top face
		indexBuffer.addTri(0, 1, 3);
		indexBuffer.addTri(1, 2, 3);

		//front face
		indexBuffer.addTri(4, 7, 6);
		indexBuffer.addTri(4, 6, 5);

		//right face
		indexBuffer.addTri(8, 11, 10);
		indexBuffer.addTri(8, 10, 9);

		//back face
		indexBuffer.addTri(12, 15, 14);
		indexBuffer.addTri(12, 14, 13);

		//left face
		indexBuffer.addTri(16, 19, 18);
		indexBuffer.addTri(16, 18, 17);

		//bottom face
		indexBuffer.addTri(20, 23, 22);
		indexBuffer.addTri(20, 22, 21);
	}

	private void buildTextCoord() {
		Vector2 textCoord = new Vector2(0, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(0, 0);
		texCoord.add(textCoord);

		textCoord = new Vector2(0, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(0, 1);
		texCoord.add(textCoord);

		textCoord = new Vector2(0, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(0, 1);
		texCoord.add(textCoord);

		textCoord = new Vector2(0, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(0, 1);
		texCoord.add(textCoord);

		textCoord = new Vector2(0, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(0, 1);
		texCoord.add(textCoord);

		textCoord = new Vector2(0, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 0);
		texCoord.add(textCoord);
		textCoord = new Vector2(1, 1);
		texCoord.add(textCoord);
		textCoord = new Vector2(0, 1);
		texCoord.add(textCoord);
	}


	public void init(GL3 gl) {
		vertexNormalBuffer.initalise(gl);
		indexBuffer.initalise(gl);
		texCoord.initalise(gl);
		vertexArray.initalise(gl);
		shader.initialise(gl);
		shader.compileAndLinkShaderProgramFromResource(gl, vertShader, fragShader);

		u_color = shader.getUniform("u_color");
		u_color.setValue(color);
		u_textured = shader.getUniform("u_textured");
		u_textured.setValue(isTextured);
		u_texture0 = shader.getUniform("u_texture0");
		u_texture0.setValue(0);

		shader.linkupDynamicUniforms();

		if (isTextured) {
			texture.initalise(gl, textureResource, false);
		}
	}

	public void render(GL3 gl, RenderState renderState) {
		renderState.applyRenderStateRequest(gl, getRenderStateRequest());

		vertexArray.bind(gl);
		vertexArray.clean(gl, shader);

		shader.bind(gl);
		shader.clean(gl, this);

		if (isTextured) {
			texture.bind(gl, 0);
			texture.clean(gl);
		}

		gl.glDrawRangeElements(
				GL3.GL_TRIANGLES, // Mode
				0, // Start
				vertexArray.getMaxIndex(), // End
				indexBuffer.getCount(), // Count
				indexBuffer.getGLType(), // index type
				0);							// Index offset

	}

	public void setColor(Color color) {
		this.color = color;
		if (u_color != null) {
			u_color.setValue(color);
		}
	}

	public Color getColor() {
		return color;
	}

	public void setTextureResource(String textureResource) {
		this.textureResource = textureResource;
		setTextured(true);
	}

	public void setTextured(boolean textured) {
		this.isTextured = textured;
		if (u_textured != null) {
			u_textured.setValue(textured);
		}
	}
}
