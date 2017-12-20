package engine.world.particles;

import org.lwjgl.util.vector.Matrix4f;

import engine.rendering.ShaderProgram;

/**
 * @Description: Loads the particle variables to the shader files
 * 
 */

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/engine/world/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "/engine/world/particles/particleFShader.txt";

	private int location_projectionMatrix;
	private int location_numberOfRows;

	/* Constructor Method */
	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	/* Gets the location of the uniform variables in the shader */
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
	}

	/* Loads the VAOs to the shader */
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelViewMatrix");
		super.bindAttribute(5, "texOffsets");
		super.bindAttribute(6, "blendFactor");
	}

	/* Loads the number of rows in the texture to the shader */
	protected void loadNumberOfRows(int numRows) {
		super.loadFloat(location_numberOfRows, numRows);
	}

	/* Loads the projection matrix to the shader */
	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}

}
