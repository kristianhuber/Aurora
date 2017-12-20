package engine.world.particles;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.MasterRenderer;
import engine.rendering.models.ModelManager;
import engine.rendering.models.RawModel;
import engine.util.Calculator;
import engine.util.Engine;

/**
 * @Description: Renders the particles
 * 
 */

public class ParticleRenderer {

	private final int INSTANCE_DATA_LENGTH = 21;
	private final int MAX_INSTANCES = 10000;
	private final FloatBuffer BUFFER;

	private ParticleShader shader;
	private RawModel quad;

	private int pointer, vbo;

	/* Constructor Method */
	protected ParticleRenderer() {

		// The buffer holds all of the data for the particle type, pointer iterates
		this.BUFFER = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
		this.pointer = 0;

		// Creates a VBO with the maximum possible length of data
		this.vbo = ModelManager.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);

		// Creates a model to render on
		quad = ModelManager.loadToVAO(new float[] { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f }, 2);

		// Sets where the attributes are in the array, I.E. VAO 1, 2, ... 6
		ModelManager.addInstancedAtrribute(quad.getVaoID(), vbo, 1, 4, INSTANCE_DATA_LENGTH, 0);
		ModelManager.addInstancedAtrribute(quad.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 4);
		ModelManager.addInstancedAtrribute(quad.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 8);
		ModelManager.addInstancedAtrribute(quad.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 12);
		ModelManager.addInstancedAtrribute(quad.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 16);
		ModelManager.addInstancedAtrribute(quad.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 20);

		// Initialize the shader
		shader = new ParticleShader();
		shader.start();
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		shader.stop();
	}

	/* Renders particles */
	protected void render(Map<ParticleTexture, List<Particle>> particles) {

		Matrix4f viewMatrix = Calculator.createViewMatrix(Engine.getCamera());

		// Start Rendering
		shader.start();
		this.prepare();

		// Run through the different types of particles
		for (ParticleTexture texture : particles.keySet()) {

			// Sets the texture for the group of particles
			this.bindTexture(texture);

			// List of all the instances in the particle group
			List<Particle> particleList = particles.get(texture);

			// List with the data for all particles
			pointer = 0;
			float[] vboData = new float[particleList.size() * INSTANCE_DATA_LENGTH];

			// Renders each individual particle
			for (Particle particle : particleList) {
				this.updateModelViewMatrix(particle.getPosition(), particle.getRotation(), particle.getScale(),
						viewMatrix, vboData);
				this.updateTexCoordInfo(particle, vboData);
			}

			// Draws a batch of particles
			ModelManager.updateVBO(vbo, vboData, BUFFER);
			GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), particleList.size());
		}

		// Finish Rendering
		this.finishRendering();
		shader.stop();
	}

	/* Stores particle texture data in an array */
	private void updateTexCoordInfo(Particle particle, float[] data) {
		data[pointer++] = particle.getTexOffset1().x;
		data[pointer++] = particle.getTexOffset1().y;
		data[pointer++] = particle.getTexOffset2().x;
		data[pointer++] = particle.getTexOffset2().y;
		data[pointer++] = particle.getBlend();
	}

	/* Loads the texture settings to the shader */
	private void bindTexture(ParticleTexture texture) {

		// Settings for additive textures
		if (texture.isAdditive()) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}

		// Binds the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());

		// Loads the number of rows on the texture
		shader.loadNumberOfRows(texture.getNumberOfRows());
	}

	/* Updates the ModelView matrix */
	private void updateModelViewMatrix(Vector3f position, float rotation, float scale, Matrix4f viewMatrix,
			float[] vboData) {

		Matrix4f modelMatrix = new Matrix4f();

		// Translation
		Matrix4f.translate(position, modelMatrix, modelMatrix);

		// Combining
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;

		// Scaling and rotation
		Matrix4f modelViewMatrix = Matrix4f.mul(viewMatrix, modelMatrix, null);
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1), modelViewMatrix, modelViewMatrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), modelViewMatrix, modelViewMatrix);

		// Convert from matrix to array
		this.storeMatrixData(modelViewMatrix, vboData);
	}

	/* Stores matrix data in an array */
	private void storeMatrixData(Matrix4f matrix, float[] vboData) {
		vboData[pointer++] = matrix.m00;
		vboData[pointer++] = matrix.m01;
		vboData[pointer++] = matrix.m02;
		vboData[pointer++] = matrix.m03;
		vboData[pointer++] = matrix.m10;
		vboData[pointer++] = matrix.m11;
		vboData[pointer++] = matrix.m12;
		vboData[pointer++] = matrix.m13;
		vboData[pointer++] = matrix.m20;
		vboData[pointer++] = matrix.m21;
		vboData[pointer++] = matrix.m22;
		vboData[pointer++] = matrix.m23;
		vboData[pointer++] = matrix.m30;
		vboData[pointer++] = matrix.m31;
		vboData[pointer++] = matrix.m32;
		vboData[pointer++] = matrix.m33;
	}

	/* Enables VAOs and settings */
	private void prepare() {
		GL30.glBindVertexArray(quad.getVaoID());

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
	}

	/* Disables VAOs and settings */
	private void finishRendering() {
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);

		GL20.glDisableVertexAttribArray(6);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);

		GL30.glBindVertexArray(0);
	}

	/* Cleans up memory */
	protected void cleanUp() {
		shader.cleanUp();
	}
}