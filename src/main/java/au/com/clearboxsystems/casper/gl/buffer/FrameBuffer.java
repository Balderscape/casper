package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: pauls
 * Timestamp: 6/01/14 2:18 PM
 */
public class FrameBuffer {
	private static final Logger logger = LoggerFactory.getLogger(FrameBuffer.class);

	private boolean isInitialised;
	private int glObjectIdx;

	// FIXME: Look up number of supported color attachments from GL Caps
	private static final int NUM_ATTACH = 8;
	private Texture[] colorAttachments = new Texture[NUM_ATTACH];
	private boolean[] areColorAttachmentsDirty = new boolean[NUM_ATTACH];
	private boolean isColorAttachmentDirty;

	private RenderBuffer depthAttachment;
	private boolean isDepthAttachmentDirty;

	private RenderBuffer stencilAttachment;
	private boolean isStencilAttachmentDirty;

	private RenderBuffer depthStencilAttachment;
	private boolean isDepthStencilAttachmentDirty;

	public FrameBuffer() {
		isInitialised = false;
		glObjectIdx = -1;
	}

	public void initalise(GL3 gl) {
		if (isInitialised) {
			logger.warn("Reinitialised Frame Buffer that was already initialsed, This WILL cause a memory leak on the GPU!!");
		} else {
			int [] arg = new int[1];
			gl.glGenFramebuffers(1, arg, 0);
			glObjectIdx = arg[0];
			isInitialised = true;
		}
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			int [] arg = {glObjectIdx};
			gl.glDeleteFramebuffers(1, arg, 0);
			glObjectIdx = -1;
			isInitialised = false;
		}
	}

	public void bind(GL3 gl) {
		if (!isInitialised) {
			logger.error("Attempted to bind an uninitalised Vertex Buffer");
			return;
		}
		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, glObjectIdx);
	}

	public static void unbind(GL3 gl) {
		gl.glBindFramebuffer(GL3.GL_FRAMEBUFFER, 0);
	}

	public void setColorAttachment(Texture tex, int attachPoint) {
		if (attachPoint >= colorAttachments.length) {
			logger.error("Cannot attach a color attachment to attach point " + attachPoint + " as there are only " + colorAttachments.length + " attach points available");
			return;
		}
		colorAttachments[attachPoint] = tex;
		areColorAttachmentsDirty[attachPoint] = true;
		isColorAttachmentDirty = true;
	}

	public void setDepthAttachment(RenderBuffer rb) {
		depthAttachment = rb;
		isDepthAttachmentDirty = true;
	}

	public void setStencilAttachment(RenderBuffer rb) {
		stencilAttachment = rb;
		isStencilAttachmentDirty = true;
	}

	public void setDepthStencilAttachment(RenderBuffer rb) {
		depthStencilAttachment = rb;
		isDepthStencilAttachmentDirty = true;
	}

	public void clean(GL3 gl) {
		if (isColorAttachmentDirty) {
			int[] drawBuffers = new int[colorAttachments.length];
			for (int i = 0; i < colorAttachments.length; i++) {
				if (areColorAttachmentsDirty[i]) {
					if (colorAttachments[i] == null) {
						gl.glFramebufferTexture(GL3.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0 + i, 0, 0);
					} else {
						gl.glFramebufferTexture(GL3.GL_FRAMEBUFFER, GL.GL_COLOR_ATTACHMENT0 + i, colorAttachments[i].getGLHandle(gl), 0);
						drawBuffers[i] = GL.GL_COLOR_ATTACHMENT0 + i;
					}
					areColorAttachmentsDirty[i] = false;
				}
			}
			gl.glDrawBuffers(colorAttachments.length, drawBuffers, 0);
			isColorAttachmentDirty = false;
		}

		if (isDepthAttachmentDirty) {
			gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_ATTACHMENT, GL3.GL_RENDERBUFFER, depthAttachment == null ? 0 : depthAttachment.getGLHandle());
			isDepthAttachmentDirty = false;
		}

		if (isStencilAttachmentDirty) {
			gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_STENCIL_ATTACHMENT, GL3.GL_RENDERBUFFER, stencilAttachment == null ? 0 : stencilAttachment.getGLHandle());
			isStencilAttachmentDirty = false;
		}

		if (isDepthStencilAttachmentDirty) {
			gl.glFramebufferRenderbuffer(GL3.GL_FRAMEBUFFER, GL3.GL_DEPTH_STENCIL_ATTACHMENT, GL3.GL_RENDERBUFFER, depthStencilAttachment == null ? 0 : depthStencilAttachment.getGLHandle());
			isDepthStencilAttachmentDirty = false;
		}
	}
}
