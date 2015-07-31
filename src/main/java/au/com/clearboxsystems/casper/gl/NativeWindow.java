package au.com.clearboxsystems.casper.gl;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.scene.Scene;
import com.jogamp.newt.event.*;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: pauls
 * Timestamp: 6/01/14 11:41 AM
 */
public class NativeWindow {

	private static final Logger logger = LoggerFactory.getLogger(NativeWindow.class);

	private final GLWindow glWindow;
	private final FPSAnimator animator;

	private Scene scene;

	private boolean fullscreen = false;
	private boolean exitOnClose = false;

	public NativeWindow(String title) {
		GLProfile glProfile = GLProfile.get(GLProfile.GL3);
		GLCapabilities caps = new GLCapabilities(glProfile);
		caps.setSampleBuffers(true);

		glWindow = GLWindow.create(caps);
		glWindow.setTitle(title);
		glWindow.setSize(640, 480);

		animator = new FPSAnimator(glWindow, 60);

		glWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDestroyNotify(WindowEvent windowEvent) {
				new Thread() {
					@Override
					public void run() {
						if(animator.isStarted()) animator.stop();
						if(exitOnClose) System.exit(0);
					}
				}.start();
			}
		});

		glWindow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				switch(keyEvent.getKeyCode()) {
					case KeyEvent.VK_ENTER:
						if (keyEvent.isAltDown() || keyEvent.isAltGraphDown()) {
							logger.info("Alt-Enter pressed, entering / exiting full screen");
							setFullscreen(!fullscreen);
						}
				}
			}
		});
		glWindow.setVisible(true);
	}

	public boolean isExitOnClose() {
		return exitOnClose;
	}

	public void setExitOnClose(boolean exitOnClose) {
		this.exitOnClose = exitOnClose;
	}

	public void setScene(Scene scene) {
		if (this.scene != null)
			glWindow.removeGLEventListener(this.scene);

		if (scene != null)
			glWindow.addGLEventListener(scene);

		this.scene = scene;
	}

	public void start() {
		animator.start();
	}

	public void stop() {
		animator.stop();
	}

	private void setFullscreen(boolean fullscreen) {
		if (fullscreen != this.fullscreen) {
			glWindow.setUndecorated(fullscreen);
			glWindow.setAlwaysOnTop(fullscreen);
			glWindow.setFullscreen(fullscreen);
//			glWindow.setPointerVisible(true);
//			glWindow.confinePointer(true);
			this.fullscreen = fullscreen;
		}
	}

	public void addMouseListener(MouseListener mouseListener) {
		glWindow.addMouseListener(mouseListener);
	}

	public void removeMouseListener(MouseListener mouseListener) {
		glWindow.removeMouseListener(mouseListener);
	}

	public void addKeyListener(KeyListener keyListener) {
		glWindow.addKeyListener(keyListener);
	}

	public void removeKeyListener(KeyListener keyListener) {
		glWindow.removeKeyListener(keyListener);

	}

	public int getWidth() {
		return glWindow.getWidth();
	}

	public int getHeight() {
		return glWindow.getHeight();
	}

}
