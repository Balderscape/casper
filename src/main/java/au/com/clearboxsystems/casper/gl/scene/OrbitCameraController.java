package au.com.clearboxsystems.casper.gl.scene;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.NativeWindow;
import au.com.clearboxsystems.casper.math.Quaternion;
import au.com.clearboxsystems.casper.math.Vector3;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * User: pauls
 * Timestamp: 23/01/14 4:00 PM
 */
public class OrbitCameraController implements MouseListener, KeyListener {
	private static final Logger logger = LoggerFactory.getLogger(OrbitCameraController.class);

	private Camera camera;
	private NativeWindow window;

	private boolean mouseEnabled = false;
	private boolean keyEnabled = false;

	private Vector3 target;
	private Quaternion orientation = new Quaternion();
	private double range;

	private boolean rightButtonDown = false;
	int dx, dy;
	int lastX, lastY;

	public OrbitCameraController(Camera camera, NativeWindow window) {
		this.camera = camera;
		this.window = window;

		target = new Vector3();
		range = camera.getPosition().nSub(target).mag();
		setMouseEnabled(true);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				updateOrientation();
				updateCamera();
			}
		}, 0, 20);
	}

	private void updateOrientation() {
		double xMove = (double) dx / (double) window.getWidth();
		double yMove = (double) dy / (double) window.getHeight();

		double rotationRate = 2 * Math.PI;

		if (xMove != 0 || yMove != 0) {
			Quaternion xChange = new Quaternion(xMove * rotationRate, new Vector3(0, 0, 1));
			Quaternion yChange = new Quaternion(yMove * rotationRate, new Vector3(0, 1, 0));

			orientation = xChange.times(orientation);
			orientation = yChange.times(orientation);
			orientation.norm();
		}

		dx = dy = 0;
	}

	private void updateCamera() {
		Vector3 newCameraPosition = orientation.rotate(target.nAdd(range, 0, 0));
		Vector3 newLookDirection = target.nSub(newCameraPosition).norm();
		Vector3 newUp = orientation.rotate(new Vector3(0, 0, 1));

		camera.setPosition(newCameraPosition);
		camera.setLookDirection(newLookDirection);
		camera.setUp(newUp);
	}

	private void setMouseEnabled(boolean mouseEnabled) {
		if (this.mouseEnabled != mouseEnabled) {
			if (mouseEnabled) {
				window.addMouseListener(this);
			} else {
				window.removeMouseListener(this);
			}
			this.mouseEnabled = mouseEnabled;
		}
	}

	private void setKeyEnabled(boolean keyEnabled) {
		if (this.keyEnabled != keyEnabled) {
			if (keyEnabled) {
				window.addKeyListener(this);
			} else {
				window.removeKeyListener(this);
			}
			this.keyEnabled = keyEnabled;
		}
	}

	public void keyPressed(KeyEvent keyEvent) {
	}

	public void keyReleased(KeyEvent keyEvent) {
	}

	public void mouseClicked(MouseEvent mouseEvent) {
	}

	public void mouseEntered(MouseEvent mouseEvent) {
	}

	public void mouseExited(MouseEvent mouseEvent) {
	}

	public void mousePressed(MouseEvent mouseEvent) {
		if ((mouseEvent.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
			rightButtonDown = true;
		}
		lastX = mouseEvent.getX();
		lastY = mouseEvent.getY();
	}

	public void mouseReleased(MouseEvent mouseEvent) {
		if ((mouseEvent.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
			rightButtonDown = false;
			dx = 0;
			dy = 0;
		}
	}

	public void mouseMoved(MouseEvent mouseEvent) {
		if (!rightButtonDown)
			return;

		dx += mouseEvent.getX() - lastX;
		dy += mouseEvent.getY() - lastY;
		lastX = mouseEvent.getX();
		lastY = mouseEvent.getY();
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		mouseMoved(mouseEvent);
	}

	public void mouseWheelMoved(MouseEvent mouseEvent) {
		double zoom = mouseEvent.getRotation()[1] * 10;
		range += zoom;
	}
}
