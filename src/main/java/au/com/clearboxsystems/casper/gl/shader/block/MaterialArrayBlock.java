package au.com.clearboxsystems.casper.gl.shader.block;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.buffer.BufferType;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.UniformBlock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * User: pauls
 * Timestamp: 20/01/14 4:15 PM
 */
public class MaterialArrayBlock extends UniformBlock {

	private List<Material> materialList = new ArrayList<>();
	private boolean materialListChanged = false;

	public MaterialArrayBlock() {
		super("Material", 0, BufferType.StaticDraw);
	}

	public int addMaterial(Material material) {
		int idx = materialList.indexOf(material);
		if (idx >= 0)
			return idx;

		idx = materialList.size();
		materialList.add(material);
		materialListChanged = true;
		return idx;
	}

	@Override
	public void update(Scene scene) {
		if (materialListChanged) {
			this.setSize(materialList.size() * 48);
			this.byteBuffer = ByteBuffer.allocateDirect(this.size).order(ByteOrder.nativeOrder());

			for (int i = 0; i < materialList.size(); i++) {
				Material m = materialList.get(i);
				m.diffuseColour.toByteBufferAsFloat(this.byteBuffer, i * 48);
				m.specularColour.toByteBufferAsFloat(this.byteBuffer, i * 48 + 16);
				byteBuffer.putFloat(i * 48 + 32, m.specularShininess);
			}

			this.isDirty = true;
			this.materialListChanged = false;
		}
	}

}
