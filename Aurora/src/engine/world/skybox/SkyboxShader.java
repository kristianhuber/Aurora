package engine.world.skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.ShaderProgram;
import engine.util.Calculator;
import engine.util.Engine;

/**
 * @Description: Loads Java variables to the skybox shader
 * 
 */

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/engine/world/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "/engine/world/skybox/skyboxFragmentShader.txt";

	private static final float ROTATE_SPEED = 0.15f;

	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubeMap;
	private int location_cubeMap2;
	private int location_blendFactor;

	private float rotation;

	/* Constructor Method */
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/* Loads the VAOs to the shader */
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	/* Gets the textures to load to the shader */
	public void connectTextureUnits() {
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
	}

	/* Gets the location of each variable in the shader */
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColor = super.getUniformLocation("fogColor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
		location_blendFactor = super.getUniformLocation("blendFactor");
	}

	/* Loads the projection matrix to the shader */
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	/* Loads the view matrix to the shader */
	public void loadViewMatrix() {
		// Creates a view matrix
		Matrix4f matrix = Calculator.createViewMatrix(Engine.getCamera());
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;

		// Calculates the rotation to put in the matrix
		rotation += ROTATE_SPEED * Engine.getDelta();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);

		// Loads it to the shader
		super.loadMatrix(location_viewMatrix, matrix);
	}

	/* Loads the fog color to the shader */
	public void loadFogColor(Vector3f color) {
		super.loadVector(location_fogColor, color);
	}

	/* Loads the blend factor to the shader */
	public void loadBlendFactor(float blend) {
		super.loadFloat(location_blendFactor, blend);
	}
}
