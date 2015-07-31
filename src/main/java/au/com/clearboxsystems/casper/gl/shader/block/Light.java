package au.com.clearboxsystems.casper.gl.shader.block;

import au.com.clearboxsystems.casper.gl.buffer.BufferType;
import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.UniformBlock;
import au.com.clearboxsystems.casper.math.Vector3;
import au.com.clearboxsystems.casper.math.Vector4;

/**
 * Created with IntelliJ IDEA.
 * User: pauls
 * Date: 22/01/2014
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Light extends UniformBlock {

	private Vector4 ambientIntensity;
	private float lightAttenuation;
	private Vector4 lightPosition;
	private Vector4 lightIntensity;

	private boolean lightChanged;

	public Light(float ambient, float halfLightDistance, float intensity, Vector3 position) {
		super("Light", 64, BufferType.DynamicDraw);
		ambientIntensity = new Vector4(ambient, ambient, ambient, 1.0);
		lightAttenuation = 1.0f / (halfLightDistance * halfLightDistance);
		lightIntensity = new Vector4(intensity, intensity, intensity, 1.0);
		lightPosition = new Vector4(position.x, position.y, position.z, 1.0);
		lightChanged = true;
	}

	@Override
	public void update(Scene scene) {
		if (lightChanged) {

			ambientIntensity.toByteBufferAsFloat(this.byteBuffer, 0);
			byteBuffer.putFloat(16, lightAttenuation);
			lightPosition.toByteBufferAsFloat(this.byteBuffer, 32);
			lightIntensity.toByteBufferAsFloat(this.byteBuffer, 48);

			this.isDirty = true;
			lightChanged = false;
		}


	}
}
