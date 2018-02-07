package engine.animation.renderer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.ShaderProgram;

public class AnimatedModelShader extends ShaderProgram {

	private static final int MAX_JOINTS = 50;// max number of joints in a skeleton

	private static final String VERTEX_SHADER = "engine/animation/renderer/animatedEntityVertex.glsl";
	private static final String FRAGMENT_SHADER = "engine/animation/renderer/animatedEntityFragment.glsl";
	
	private int location_projectionViewMatrix;
	private int location_lightDirection;
	private int location_jointTransforms[];
	private int location_diffuseMap;

	// What I need to do is convert this shader and the ICamera projection view
	// matrix is just the projectionMatrix * viewMatrix, and they are both easy to
	// get.

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by loading
	 * up the vertex and fragment shader code files. It also gets the location of
	 * all the specified uniform variables, and also indicates that the diffuse
	 * texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		connectTextureUnits();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
		super.bindAttribute(2, "in_normal");
		super.bindAttribute(3, "in_jointIndices");
		super.bindAttribute(4, "in_weights");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionViewMatrix = super.getUniformLocation("projectionViewMatrix");
		location_lightDirection = super.getUniformLocation("lightDirection");
		location_diffuseMap = super.getUniformLocation("diffuseMap");
		
		location_jointTransforms = new int[MAX_JOINTS];
		for (int i = 0; i < MAX_JOINTS; i++) {
			location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		}
	}
	
	public void loadJointTransforms(Matrix4f[] t) {
		Matrix4f identity = new Matrix4f();
		identity.m00 = 1;
		identity.m11 = 1;
		identity.m22 = 1;
		identity.m33 = 1;
		
		for(int i = 0; i < t.length; i++) {
			if (i < t.length) {
				super.loadMatrix(location_jointTransforms[i], t[i]);
			} else {
				super.loadMatrix(location_jointTransforms[i], identity);
			}
		}
	}

	public void loadProjectionViewMatrix(Matrix4f mat) {
		super.loadMatrix(location_projectionViewMatrix, mat);
	}
	
	public void loadLightDirection(Vector3f light) {
		super.loadVector(location_lightDirection, light);
	}
	
	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void connectTextureUnits() {
		super.loadInt(location_diffuseMap, 0);
	}

}
