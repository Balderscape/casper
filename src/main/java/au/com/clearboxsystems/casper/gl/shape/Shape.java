package au.com.clearboxsystems.casper.gl.shape;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.scene.Renderable;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.state.RenderStateRequest;
import au.com.clearboxsystems.casper.math.Matrix3;
import au.com.clearboxsystems.casper.math.Matrix4;
import au.com.clearboxsystems.casper.math.Vector3;
import au.com.clearboxsystems.casper.math.Vector4;

/**
 * User: pauls
 * Timestamp: 6/01/14 3:16 PM
 */
public abstract class Shape implements Renderable {

	private Scene scene;
	private Shape parent;

	private Vector3 position;
	private Matrix3 rotation;
	private Matrix4 model;
	private Matrix4 invModel;
	private boolean isModelDirty;

	protected RenderStateRequest renderState;
	private Vector4 diffuseSpecularAmbientShininess = new Vector4(0.65, 0.25, 0.1, 12);

	public Shape(Scene scene, Shape parent) {
		this.scene = scene;
		this.parent = parent;

		position = new Vector3();
		rotation = Matrix3.identity();
		model = Matrix4.identity();
		invModel = Matrix4.identity();
		isModelDirty = false;
	}

	public Shape(Scene scene) {
		this(scene, null);
	}

	public Scene getScene() {
		return scene;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position.set(position);
		isModelDirty = true;
	}

	public void setRotation(Matrix3 rotation) {
		this.rotation.set(rotation);
		isModelDirty = true;
	}

	private void cleanModel() {
		model.setSubMatrix(0, 0, rotation);
		model.m14 = position.x;
		model.m24 = position.y;
		model.m34 = position.z;

		Matrix3 invRot = rotation.nTranspose();
		Vector3 invPos = invRot.nTimes(position);
		invModel.setSubMatrix(0, 0, invRot);
		invModel.m14 = -invPos.x;
		invModel.m24 = -invPos.y;
		invModel.m34 = -invPos.z;

		isModelDirty = false;
	}

	public RenderStateRequest getRenderStateRequest() {
		if (renderState == null) {
			return scene.getRenderStateRequest();
		}

		return scene.getRenderStateRequest().combine(renderState);
	}

	public Matrix4 getModelViewPerspective() {
		if (isModelDirty) {
			cleanModel();
		}

		if (parent != null) {
			return parent.getModelViewPerspective().times(model);
		}
		return scene.getViewPerspective().nTimes(model);
	}

	public Vector4 getDiffuseSpecularAmbientShininess() {
		return diffuseSpecularAmbientShininess;
	}

	public Matrix4 getModel() {
		if (isModelDirty) {
			cleanModel();
		}

		if (parent != null) {
			return parent.getModel().nTimes(model);
		}
		return model;
	}

	public Matrix4 getInvModel() {
		if (isModelDirty) {
			cleanModel();
		}

		if (parent != null) {
			return model.nTimes(parent.getInvModel());
		}
		return invModel;
	}

}
