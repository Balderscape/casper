package au.com.clearboxsystems.casper.gl.state;
/**
 * Copyright (C) 2013 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import com.jogamp.opengl.GL3;

import java.awt.*;

/**
 *
 * @author Clearbox
 */
public class Blending {

	private Boolean enabled = null;
	private SourceBlendingFactor sourceRGBFactor = null;
	private SourceBlendingFactor sourceAlphaFactor = null;
	private DestinationBlendingFactor destinationRGBFactor = null;
	private DestinationBlendingFactor destinationAlphaFactor = null;
	private BlendingEquation RGBEquation = null;
	private BlendingEquation alphaEquation = null;
	private Color color;

	public enum SourceBlendingFactor {

		Zero(GL3.GL_ZERO),
		One(GL3.GL_ONE),
		SourceAlpha(GL3.GL_SRC_ALPHA),
		OneMinusSourceAlpha(GL3.GL_ONE_MINUS_SRC_ALPHA),
		DestinationAlpha(GL3.GL_DST_ALPHA),
		OneMinusDestinationAlpha(GL3.GL_ONE_MINUS_DST_ALPHA),
		DestinationColor(GL3.GL_DST_COLOR),
		OneMinusDestinationColor(GL3.GL_ONE_MINUS_DST_COLOR),
		SourceAlphaSaturate(GL3.GL_SRC_ALPHA_SATURATE),
		ConstantColor(GL3.GL_CONSTANT_COLOR),
		OneMinusConstantColor(GL3.GL_ONE_MINUS_CONSTANT_COLOR),
		ConstantAlpha(GL3.GL_CONSTANT_ALPHA),
		OneMinusConstantAlpha(GL3.GL_ONE_MINUS_CONSTANT_ALPHA);
		public final int glType;

		private SourceBlendingFactor(int glType) {
			this.glType = glType;
		}
	}

	public enum DestinationBlendingFactor {

		Zero(GL3.GL_ZERO),
		One(GL3.GL_ONE),
		SourceAlpha(GL3.GL_SRC_ALPHA),
		OneMinusSourceAlpha(GL3.GL_ONE_MINUS_SRC_ALPHA),
		DestinationAlpha(GL3.GL_DST_ALPHA),
		OneMinusDestinationAlpha(GL3.GL_ONE_MINUS_DST_ALPHA),
		DestinationColor(GL3.GL_DST_COLOR),
		OneMinusDestinationColor(GL3.GL_ONE_MINUS_DST_COLOR),
		SourceAlphaSaturate(GL3.GL_SRC_ALPHA_SATURATE),
		ConstantColor(GL3.GL_CONSTANT_COLOR),
		OneMinusConstantColor(GL3.GL_ONE_MINUS_CONSTANT_COLOR),
		ConstantAlpha(GL3.GL_CONSTANT_ALPHA),
		OneMinusConstantAlpha(GL3.GL_ONE_MINUS_CONSTANT_ALPHA);
		public final int glType;

		private DestinationBlendingFactor(int glType) {
			this.glType = glType;
		}
	}

	public enum BlendingEquation {

		Add(GL3.GL_FUNC_ADD),
		Minimum(GL3.GL_MIN),
		Maximum(GL3.GL_MAX),
		Subtract(GL3.GL_FUNC_SUBTRACT),
		ReverseSubtract(GL3.GL_FUNC_REVERSE_SUBTRACT);
		public final int glType;

		private BlendingEquation(int glType) {
			this.glType = glType;
		}
	}

	public void applyState(GL3 gl, BlendingRequest request) {
		if (enabled == null || enabled != request.enabled) {
			enabled = request.enabled;
			if (enabled) {
				gl.glEnable(GL3.GL_BLEND);
			} else {
				gl.glDisable(GL3.GL_BLEND);
			}
		}

		if (enabled) {
			if (sourceRGBFactor != request.sourceRGBFactor
					|| sourceAlphaFactor != request.sourceAlphaFactor
					|| destinationRGBFactor != request.destinationRGBFactor
					|| destinationAlphaFactor != request.destinationAlphaFactor) {
				sourceRGBFactor = request.sourceRGBFactor;
				sourceAlphaFactor = request.sourceAlphaFactor;
				destinationRGBFactor = request.destinationRGBFactor;
				destinationAlphaFactor = request.destinationAlphaFactor;
				gl.glBlendFuncSeparate(
						sourceRGBFactor.glType,
						destinationRGBFactor.glType,
						sourceAlphaFactor.glType,
						destinationAlphaFactor.glType);
			}

			if (RGBEquation != request.RGBEquation || alphaEquation != request.alphaEquation) {
				RGBEquation = request.RGBEquation;
				alphaEquation = request.alphaEquation;
				gl.glBlendEquationSeparate(RGBEquation.glType, alphaEquation.glType);
			}

			if (color != request.color) {
				color = request.color;
				gl.glBlendColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
			}

		}

	}
}
