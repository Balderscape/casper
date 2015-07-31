package au.com.clearboxsystems.casper.gl.shader;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.scene.Scene;
import au.com.clearboxsystems.casper.gl.shader.block.CameraBlock;
import au.com.clearboxsystems.casper.gl.shape.Shape;
import au.com.clearboxsystems.casper.math.Matrix4;
import au.com.clearboxsystems.casper.math.Vector2;
import au.com.clearboxsystems.casper.math.Vector3;
import au.com.clearboxsystems.casper.math.Vector4;
import com.jogamp.opengl.GL3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: pauls
 * Timestamp: 6/01/14 3:35 PM
 */
public class DynamicUniformManager {

	private static final LinkedHashMap<String, DynamicUniform> availableUniforms = new LinkedHashMap<>();
	private static List<DynamicUniform> dynamicUniforms = new CopyOnWriteArrayList<>();
	public static LinkedHashMap<String, UniformBlock> dynamicUniformBlocks = new LinkedHashMap<>();

	static {
		dynamicUniformBlocks.put("Camera", new CameraBlock());
	}

	public static void updateDynamicUniformBlocks(Scene scene) {
		for (UniformBlock block : dynamicUniformBlocks.values())
			if (block.isInitialised) block.update(scene);
	}

	public static void cleanDynamicUniformBlocks(GL3 gl) {
		for (UniformBlock block : dynamicUniformBlocks.values())
			if (block.isInitialised) block.clean(gl);
	}

	public static DynamicUniform lookupDynamicUniform(String name) {
		return availableUniforms.get(name);
	}

	public static void addActiveUniform(DynamicUniform uniform) {
		dynamicUniforms.add(uniform);
	}

	public static void removeActiveUniform(DynamicUniform uniform) {
		dynamicUniforms.remove(uniform);
	}

	static {
		availableUniforms.put("auto_model", new DynamicUniform<Matrix4>(new UniformUpdater<Matrix4>() {
			public Matrix4 computeUniformValue(Shape shape) {
				return shape.getModel();
			}

			public void updateUniformValue(Uniform uniform, Matrix4 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_invmodel", new DynamicUniform<Matrix4>(new UniformUpdater<Matrix4>() {
			public Matrix4 computeUniformValue(Shape shape) {
				return shape.getInvModel();
			}

			public void updateUniformValue(Uniform uniform, Matrix4 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_modelViewPerspectiveMatrix", new DynamicUniform<Matrix4>(new UniformUpdater<Matrix4>() {
			public Matrix4 computeUniformValue(Shape shape) {
				return shape.getModelViewPerspective();
			}

			public void updateUniformValue(Uniform uniform, Matrix4 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_diffuseSpecularAmbientShininess", new DynamicUniform<Vector4>(new UniformUpdater<Vector4>() {
			public Vector4 computeUniformValue(Shape shape) {
				return shape.getDiffuseSpecularAmbientShininess();
			}

			public void updateUniformValue(Uniform uniform, Vector4 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_cameraPosition", new DynamicUniform<Vector3>(new UniformUpdater<Vector3>() {
			public Vector3 computeUniformValue(Shape shape) {
				return shape.getScene().getCamera().getPosition();
			}

			public void updateUniformValue(Uniform uniform, Vector3 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_cameraDirection", new DynamicUniform<Vector3>(new UniformUpdater<Vector3>() {
			public Vector3 computeUniformValue(Shape shape) {
				return shape.getScene().getCamera().getLookDirection();
			}

			public void updateUniformValue(Uniform uniform, Vector3 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_perspectiveNearPlaneDistance", new DynamicUniform<Float>(new UniformUpdater<Float>() {
			public Float computeUniformValue(Shape shape) {
				return (float)shape.getScene().getCamera().getPerspectiveNearPlane();
			}

			public void updateUniformValue(Uniform uniform, Float value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_perspectiveFarPlaneDistance", new DynamicUniform<Float>(new UniformUpdater<Float>() {
			public Float computeUniformValue(Shape shape) {
				return (float)shape.getScene().getCamera().getPerspectiveFarPlane();
			}

			public void updateUniformValue(Uniform uniform, Float value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_cameraFoV", new DynamicUniform<Vector2>(new UniformUpdater<Vector2>() {
			public Vector2 computeUniformValue(Shape shape) {
				return shape.getScene().getCamera().getFoV();
			}

			public void updateUniformValue(Uniform uniform, Vector2 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_viewTransformationMatrix", new DynamicUniform<Matrix4>(new UniformUpdater<Matrix4>() {
			public Matrix4 computeUniformValue(Shape shape) {
				return shape.getScene().getCamera().getViewTransformation();
			}

			public void updateUniformValue(Uniform uniform, Matrix4 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_viewportTransformationMatrix", new DynamicUniform<Matrix4>(new UniformUpdater<Matrix4>() {
			public Matrix4 computeUniformValue(Shape shape) {
				return shape.getScene().getCamera().getViewportTransform();
			}

			public void updateUniformValue(Uniform uniform, Matrix4 value) {
				uniform.setValue(value);
			}
		}));

		availableUniforms.put("auto_viewportOrthographicMatrix", new DynamicUniform<Matrix4>(new UniformUpdater<Matrix4>() {
			public Matrix4 computeUniformValue(Shape shape) {
				return shape.getScene().getCamera().getViewportOrthographicMatrix();
			}

			public void updateUniformValue(Uniform uniform, Matrix4 value) {
				uniform.setValue(value);
			}
		}));


	}
}
