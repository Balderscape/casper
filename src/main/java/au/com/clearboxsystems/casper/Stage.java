package au.com.clearboxsystems.casper;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.NativeWindow;
import au.com.clearboxsystems.casper.gl.scene.Camera;
import au.com.clearboxsystems.casper.gl.scene.OrbitCameraController;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.block.Light;
import au.com.clearboxsystems.casper.gl.shape.Line;
import au.com.clearboxsystems.casper.gl.shape.LineRenderer;
import au.com.clearboxsystems.casper.gl.shape.Sphere;
import au.com.clearboxsystems.casper.gl.shape.SphereRenderer;
import au.com.clearboxsystems.casper.math.Vector3;

import java.util.List;

/**
 * User: pauls
 * Timestamp: 31/07/2015 11:17 AM
 */
public class Stage {

	private NativeWindow window;
	private Camera camera;
	private LineRenderer lineRenderer;
	private SphereRenderer sphereRenderer;

	public Stage() {
		window = new NativeWindow("Casper");

		Vector3 cameraPosition = new Vector3(3000, 0, 0);
		camera = new Camera(cameraPosition, 100, 10000);

		Scene scene = new Scene(camera);

		Light light = new Light(0.4f, 2500f, 0.6f, new Vector3(1, 1, 1));
		light.update(scene);
		scene.setLight(light);

		lineRenderer = new LineRenderer(scene);
		scene.addRenderable(lineRenderer);

		sphereRenderer = new SphereRenderer(scene);
		scene.addRenderable(sphereRenderer);

		window.setScene(scene);
		window.start();

		OrbitCameraController cameraController = new OrbitCameraController(camera, window);
	}

	public void setExitOnClose(boolean exitOnClose) {
		window.setExitOnClose(exitOnClose);
	}

	public void setDirty() {
		sphereRenderer.setDirty();
	}

	public void update() {
		sphereRenderer.update();
		lineRenderer.update();
	}

	public void addSphere(Sphere sphere) {
		sphereRenderer.addSphere(sphere);
		sphereRenderer.update();
	}


	public void addSpheres(List<Sphere> spheres) {
		for (Sphere sphere : spheres)
			sphereRenderer.addSphere(sphere);

		sphereRenderer.update();
	}

	public void addLine(Line line) {
		lineRenderer.addLine(line);
		lineRenderer.update();
	}

	public void addLines(List<Line> lines) {
		for (Line line : lines)
			lineRenderer.addLine(line);

		lineRenderer.update();
	}

	public void clear() {
		sphereRenderer.clear();
		lineRenderer.clear();
		sphereRenderer.update();
		lineRenderer.update();
	}

}
