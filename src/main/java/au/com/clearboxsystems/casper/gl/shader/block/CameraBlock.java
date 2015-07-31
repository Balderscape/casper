package au.com.clearboxsystems.casper.gl.shader.block;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.buffer.BufferType;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.UniformBlock;
import au.com.clearboxsystems.casper.math.Matrix4;
import au.com.clearboxsystems.casper.math.Vector3;

/**
 * User: pauls
 * Timestamp: 16/01/14 10:14 AM
 */
public class CameraBlock extends UniformBlock {

	private Vector3 cameraWorldPosition = new Vector3();
	private Matrix4 worldToCameraMatrix = new Matrix4();
	private Matrix4 cameraToClipMatrix = new Matrix4();
	private Matrix4 worldToClipMatrix = new Matrix4();

	public CameraBlock() {
		super("Camera", 208, BufferType.DynamicDraw);
	}

	public void update(Scene scene) {
		setCameraWorldPosition(scene.getCamera().getPosition());
		setWorldToCameraMatrix(scene.getCamera().getViewTransformation());
		setCameraToClipMatrix(scene.getCamera().getPerspectiveTransformation());
		setWorldToClipMatrix(scene.getCamera().getViewPerspectiveMatrix());
	}

	public void setCameraWorldPosition(Vector3 pos) {
		cameraWorldPosition.set(pos);
		cameraWorldPosition.toByteBufferAsFloat(byteBuffer, 0);

		isDirty = true;
	}

	public void setWorldToCameraMatrix(Matrix4 mat) {
		worldToCameraMatrix.set(mat);
		worldToCameraMatrix.toByteBufferAsFloat(byteBuffer, 16, false);

		isDirty = true;
	}

	public void setCameraToClipMatrix(Matrix4 mat) {
		cameraToClipMatrix.set(mat);
		cameraToClipMatrix.toByteBufferAsFloat(byteBuffer, 80, false);

		isDirty = true;
	}

	public void setWorldToClipMatrix(Matrix4 mat) {
		worldToClipMatrix.set(mat);
		worldToClipMatrix.toByteBufferAsFloat(byteBuffer, 144, false);

		isDirty = true;
	}

}
