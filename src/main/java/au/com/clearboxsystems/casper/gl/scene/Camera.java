package au.com.clearboxsystems.casper.gl.scene;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */


import au.com.clearboxsystems.casper.math.Matrix4;
import au.com.clearboxsystems.casper.math.Vector2;
import au.com.clearboxsystems.casper.math.Vector3;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:41 PM
 */
public class Camera {
	private Scene scene;
	private Vector3 position;
	private Vector3 lookDirection;
	private Vector3 up;
	private double nearPlane;
	private double farPlane;
	private double fovY;
	private double aspectRatio;
	private double cx, cy;
	private boolean projectionDirty = true;
	private double left, right, top, bottom;
	private int vx, vy, vw, vh;

	public Camera(Vector3 position, double near, double far) {
		this.position = position;
		lookDirection = new Vector3(-1, 0, 0);
		up = new Vector3(0, 0, 1);

		fovY = Math.PI / 6;
		aspectRatio = 1;

		nearPlane = near;
		farPlane = far;
		cx = 0;
		cy = 0;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	private void updateProjection() {
		double dy = Math.tan(fovY * 0.5);
		top = cy + nearPlane * dy;
		bottom = cy - nearPlane * dy;

		double dx = dy * aspectRatio;
		left = cx - nearPlane * dx;
		right = cx + nearPlane * dx;
		projectionDirty = false;
	}

	public Matrix4 getViewTransformation() {
		Vector3 f = lookDirection.nNorm();
		Vector3 s = f.nCross(up).norm();
		Vector3 u = s.nCross(f).norm();

		return new Matrix4(
				s.x, s.y, s.z, -s.dot(position),
				u.x, u.y, u.z, -u.dot(position),
				-f.x, -f.y, -f.z, f.dot(position),
				0, 0, 0, 1);
	}

	public Matrix4 getPerspectiveTransformation() {
		if (projectionDirty) {
			updateProjection();
		}

		return new Matrix4(
				2 * nearPlane / (right - left), 0, (right + left) / (right - left), 0,
				0, 2 * nearPlane / (top - bottom), (top + bottom) / (top - bottom), 0,
				0, 0, -(farPlane + nearPlane) / (farPlane - nearPlane), -(2 * nearPlane * farPlane) / (farPlane - nearPlane),
				0, 0, -1, 0);
	}

	public Matrix4 getViewPerspectiveMatrix() {
		return getPerspectiveTransformation().times(getViewTransformation());
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 p) {
		position.x = p.x;
		position.y = p.y;
		position.z = p.z;
	}

	public void setPosition(double x, double y, double z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public Vector3 getUp() {
		return up;
	}

	public void setUp(Vector3 up) {
		this.up.x = up.x;
		this.up.y = up.y;
		this.up.z = up.z;
	}

	public void setUp(double x, double y, double z) {
		up.x = x;
		up.y = y;
		up.z = z;
	}

	public Vector3 getLookDirection() {
		return lookDirection;
	}

	public void setLookDirection(Vector3 lookDirection) {
		this.lookDirection = lookDirection;
	}

	public double getPerspectiveNearPlane() {
		return nearPlane;
	}

	public double getPerspectiveFarPlane() {
		return farPlane;
	}

	public Vector2 getFoV() {
		return new Vector2(fovY * aspectRatio, fovY);
	}

	public void setViewport(int x, int y, int width, int height) {
		aspectRatio = ((double) width) / ((double) height);
		vx = x;
		vy = y;
		vw = width;
		vh = height;

		projectionDirty = true;
	}

	public Matrix4 getViewportTransform() {
		double near = scene.getDepthRangeNear();
		double far = scene.getDepthRangeFar();

		double halfDepthRange = 0.5 * (far - near);

		return new Matrix4(
				vw / 2.,	0,			0,		vx + vw/2.,
				0,			vh/2.,		0,		vy + vh/2.,
				0,			0,			halfDepthRange,	near + halfDepthRange,
				0,			0,			0,		1);
	}

	public Matrix4 getViewportOrthographicMatrix() {
		double near = scene.getDepthRangeNear();
		double far = scene.getDepthRangeFar();

		return new Matrix4(
				2. / vw,	0,			0,		-(vx + vx + vw) / vw,
				0,			2. / vh,	0,		-(vy + vy + vh) / vh,
				0,			0,			-2 / (far - near),		-(far + near) / (far - near),
				0,			0,			0,		1);
	}
}
