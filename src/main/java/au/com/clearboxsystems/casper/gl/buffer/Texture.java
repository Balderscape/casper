package au.com.clearboxsystems.casper.gl.buffer;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: pauls
 * Timestamp: 6/01/14 2:20 PM
 */
public class Texture {
	private static final Logger logger = LoggerFactory.getLogger(Texture.class);

	private boolean isInitialised;
	private com.jogamp.opengl.util.texture.Texture texture;

	private TextureWrap wrapS;
	private boolean isWrapSDirty = false;
	private TextureWrap wrapT;
	private boolean isWrapTDirty = false;
	private TextureMinFilter minFilter;
	private boolean isMinFilterDirty = false;
	private TextureMagFilter magFilter;
	private boolean isMagFilterDirty = false;

	public int getGLHandle(GL3 gl) {
		return texture.getTextureObject(gl);
	}

	public enum TextureWrap {
		ClampEdge(GL3.GL_CLAMP_TO_EDGE),
		ClampBorder(GL3.GL_CLAMP_TO_BORDER),
		Repeat(GL3.GL_REPEAT),
		MirroredRepeat(GL3.GL_MIRRORED_REPEAT);

		public final int glType;

		private TextureWrap(int glType) {
			this.glType = glType;
		}
	}

	public enum TextureMinFilter {
		Nearest(GL3.GL_NEAREST),
		Linear(GL3.GL_LINEAR),
		NearestMipmapNearest(GL3.GL_NEAREST_MIPMAP_NEAREST),
		LinearMipmapNearest(GL3.GL_LINEAR_MIPMAP_NEAREST),
		NearestMipmapLinear(GL3.GL_NEAREST_MIPMAP_LINEAR),
		LinearMipmapLinear(GL3.GL_LINEAR_MIPMAP_LINEAR);

		public final int glType;

		private TextureMinFilter(int glType) {
			this.glType = glType;
		}
	}

	public enum TextureMagFilter {
		Nearest(GL3.GL_NEAREST),
		Linear(GL3.GL_LINEAR);

		public final int glType;

		private TextureMagFilter(int glType) {
			this.glType = glType;
		}
	}

	public Texture() {
		isInitialised = false;
	}

	private void loadTextureDataFromResource(String textureResource, boolean generateMipmap) {
		InputStream is = Texture.class.getResourceAsStream(textureResource);
		String fileSuffix = null;
		int idxOfDot = textureResource.lastIndexOf('.');
		if (idxOfDot > 0)
			fileSuffix = textureResource.substring(idxOfDot + 1);
		try {
			texture = TextureIO.newTexture(is, generateMipmap, fileSuffix);
		} catch (IOException ex) {
			logger.error("Failed to load texture from resource", ex);
		} catch (GLException ex) {
			logger.error("Failed to load texture from resource", ex);
		}
	}

	public void initalise(GL3 gl, String textureResource, boolean generateMipmap) {
		loadTextureDataFromResource(textureResource, generateMipmap);
		if (texture == null) {
			logger.error("Failed to load texure " + textureResource);
			return;
		}

		if (isInitialised) {
			logger.warn("Reinitialised Texture that was already initialsed, This is probably a code error!!");
		}

		texture.enable(gl);
		isInitialised = true;
	}


	public void initalise(GL3 gl, TextureData td) {
		if (isInitialised) {
			logger.warn("Reinitialised Texture that was already initialsed, This is probably a code error!!");
		}

		texture = new com.jogamp.opengl.util.texture.Texture(gl, td);
		texture.enable(gl);
		isInitialised = true;
	}

	public void destroy(GL3 gl) {
		if (isInitialised) {
			texture.destroy(gl);
			isInitialised = false;
		}
	}

	public void bind(GL3 gl, int textureUnit) {
		if (!isInitialised) {
			logger.error("Attempted to bind an uninitalised Vertex Buffer");
			return;
		}
		gl.glActiveTexture(GL3.GL_TEXTURE0 + textureUnit);
		texture.bind(gl);
	}

	public void clean(GL3 gl) {
		if (isWrapSDirty) {
			texture.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_S, wrapS.glType);
			isWrapSDirty = false;
		}
		if (isWrapTDirty) {
			texture.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_T, wrapT.glType);
			isWrapTDirty = false;
		}
		if (isMinFilterDirty) {
			texture.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, minFilter.glType);
			isMinFilterDirty = false;
		}
		if (isMagFilterDirty) {
			texture.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, magFilter.glType);
			isMagFilterDirty = false;
		}
	}

	public TextureWrap getWrapS() {
		return wrapS;
	}

	public void setWrapS(TextureWrap wrapS) {
		this.wrapS = wrapS;
		isWrapSDirty = true;
	}

	public TextureWrap getWrapT() {
		return wrapT;
	}

	public void setWrapT(TextureWrap wrapT) {
		this.wrapT = wrapT;
		isWrapTDirty = true;
	}

	public TextureMagFilter getMagFilter() {
		return magFilter;
	}

	public void setMagFilter(TextureMagFilter magFilter) {
		this.magFilter = magFilter;
		isMagFilterDirty = true;
	}

	public TextureMinFilter getMinFilter() {
		return minFilter;
	}

	public void setMinFilter(TextureMinFilter minFilter) {
		this.minFilter = minFilter;
		isMinFilterDirty = true;
	}


}
