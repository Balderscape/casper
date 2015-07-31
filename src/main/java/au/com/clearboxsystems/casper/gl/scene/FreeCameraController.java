package au.com.clearboxsystems.casper.gl.scene;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.NativeWindow;
import au.com.clearboxsystems.casper.math.Matrix3;
import au.com.clearboxsystems.casper.math.Quaternion;
import au.com.clearboxsystems.casper.math.Vector3;
import com.jogamp.newt.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: pauls
 * Timestamp: 6/01/14 4:28 PM
 */
public class FreeCameraController implements MouseListener, KeyListener {
	private static final Logger logger = LoggerFactory.getLogger(FreeCameraController.class);

	private Camera camera;
	private NativeWindow window;
	private double rotateFactor;
	private double rotateRateRangeAdjustment;
	private double minimumRotateRate;
	private double maximumRotateRate;
	private double speed = 0.1;
	private boolean rightButtonDown;
	private boolean wButtonDown;
	private boolean aButtonDown;
	private boolean sButtonDown;
	private boolean dButtonDown;
	private boolean qButtonDown;
	private boolean eButtonDown;
	private boolean rButtonDown;
	private boolean fButtonDown;
	private Point lastPoint;
	private double range;
	private boolean mouseEnabled = false;
	private boolean keyEnabled = false;
	private Vector3 position = new Vector3(0, 0, 0);
	int dx, dy;
	private Timer timer;

	private Quaternion q = Quaternion.fromEuler(0, 0, Math.PI);
	private double roll = 0;
	private double pitch;
	private double yaw;

	private double rate = 1;
	private Matrix3 dcm = q.getDCM().transpose();

	private TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			move();
			if (rightButtonDown) {
				rotate();
			} else {
				pitch = 0;
				yaw = 0;
			}

			Quaternion dq = q.nDeriv(new Vector3(roll, pitch, yaw));
			q.add(dq.times(rate * 0.5));
			q.norm();
			dcm = q.getDCM().transpose();
			updateCameraFromParameters();
		}
	};

	public FreeCameraController(Camera camera, NativeWindow window, double maxViewRadius) {
		this.camera = camera;
		this.window = window;

		rotateFactor = 1.0 / maxViewRadius;
		rotateRateRangeAdjustment = maxViewRadius;
		maximumRotateRate = 1.0 / 50;
		minimumRotateRate = 1.0 / 5000.0;

		range = maxViewRadius * 2.0;

		position = camera.getPosition();

		setMouseEnabled(true);
		setKeyEnabled(true);

		timer = new Timer();
		timer.schedule(timerTask, 0, 20);
	}

	public void updateCameraFromParameters() {
		camera.setPosition(position);

		camera.setLookDirection(dcm.times(new Vector3(1, 0, 0)));
		camera.setUp(dcm.times(new Vector3(0, 0, 1)));
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
			rightButtonDown = true;
		}
		lastPoint = new Point(e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
			rightButtonDown = false;
			dx = 0;
			dy = 0;
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		if (!rightButtonDown) {
			return;
		}

		dx += e.getX() - lastPoint.x;
		dy += e.getY() - lastPoint.y;
		lastPoint.x = e.getX();
		lastPoint.y = e.getY();
	}

	public void mouseWheelMoved(MouseEvent mouseEvent) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.isAutoRepeat())
			return;

		if (e.getKeyCode() == KeyEvent.VK_W) {
			wButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			aButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			sButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			dButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			qButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			eButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_R) {
			rButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_F) {
			fButtonDown = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			rate = 4;
			speed = 1;
		}

	}

	public void keyReleased(KeyEvent e) {
		if (e.isAutoRepeat())
			return;

		if (e.getKeyCode() == KeyEvent.VK_W) {
			wButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			aButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			sButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			dButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_Q) {
			qButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_E) {
			eButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_R) {
			rButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_F) {
			fButtonDown = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			rate = 1;
			speed = 1;
		}
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

	private void move() {
		Vector3 forwardDir = dcm.times(new Vector3(1, 0, 0));
		Vector3 sideDir = dcm.times(new Vector3(0, 1, 0));
		Vector3 upDir = dcm.times(new Vector3(0, 0, 1));

		if (wButtonDown && !sButtonDown) {
			position = position.add(forwardDir.nScale(speed));
		}
		if (sButtonDown && !wButtonDown) {
			position = position.add(forwardDir.nScale(-speed));
		}
		if (aButtonDown && !dButtonDown) {
			position = position.add(sideDir.nScale(speed));
		}
		if (dButtonDown && !aButtonDown) {
			position = position.add(sideDir.nScale(-speed));
		}
		if (rButtonDown && !fButtonDown) {
			position = position.add(upDir.nScale(speed));
		}
		if (fButtonDown && !rButtonDown) {
			position = position.add(upDir.nScale(-speed));
		}
		if (qButtonDown && !eButtonDown) {
			roll = 0.05 ;
		} else if (eButtonDown && !qButtonDown) {
			roll = -0.05;
		} else {
			roll = 0;
		}
	}

	private void rotate() {

		double yawWindowRatio = (double) dx / (double) window.getWidth();
		double pitchWindowRatio = (double) dy / (double) window.getHeight();

		yaw = 0.3 * yawWindowRatio * Math.PI * 2.;
		pitch = -0.3 * pitchWindowRatio * Math.PI;

		dx = 0;
		dy = 0;
	}

	public Quaternion getQuaternion(){
		return q;
	}

}
