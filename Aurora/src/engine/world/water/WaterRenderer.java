package engine.world.water;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.rendering.MasterRenderer;
import engine.rendering.models.ModelManager;
import engine.rendering.models.RawModel;
import engine.rendering.textures.TextureManager;
import engine.util.Calculator;
import engine.util.Engine;
import engine.world.World;
import engine.world.entities.Light;

/**
 * @Description: Renders the water in the game
 * 
 */

public class WaterRenderer {

	private static final float WAVE_SPEED = 0.08f;

	private WaterFrameBuffers fbos;
	private WaterShader shader;
	private RawModel quad;

	private int dudvTexture, normalMap;
	private float moveFactor;

	/* Constructor Method */
	public WaterRenderer() {
		// Set up the variables
		this.dudvTexture = TextureManager.getTexture("dudvMap").getID();
		this.normalMap = TextureManager.getTexture("normalMap").getID();
		this.fbos = new WaterFrameBuffers();
		this.shader = new WaterShader();
		this.moveFactor = 0;

		// Initializes the shader
		shader.start();
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(MasterRenderer.getProjectionMatrix());
		shader.stop();

		// Loads a quad
		this.setUpVAO();
	}

	/* Renders the water */
	public void render(World w, Light sun, Vector3f skyColor) {

		// Prepares shader settings
		this.prepareRender(sun, skyColor);

		// Renders each of the water tiles
		for (WaterTile tile : w.getRenderedWaters()) {

			// Creates a model matrix
			Matrix4f modelMatrix = Calculator
					.createTransformationMatrix(new Vector3f(tile.getX() + WaterTile.SIZE / 2, tile.getHeight(),
							tile.getZ() + WaterTile.SIZE / 2), new Vector3f(0, 0, 0), WaterTile.SIZE / 2);
			shader.loadModelMatrix(modelMatrix);

			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}

		// Disables the shader
		this.unbind();
	}

	/* Enables the reflection frame buffer */
	public void bindReflectionFrameBuffer() {
		this.fbos.bindReflectionFrameBuffer();
	}

	/* Enables the refraction frame buffer */
	public void bindRefractionFrameBuffer() {
		this.fbos.bindRefractionFrameBuffer();
	}

	/* Disables any frame buffer */
	public void unbindCurrentFrameBuffer() {
		this.fbos.unbindCurrentFrameBuffer();
	}

	/* Loads the settings to the shader */
	private void prepareRender(Light sun, Vector3f skyColor) {

		// Start the shader and load the settings
		shader.start();

		shader.loadViewMatrix();
		shader.loadPlaneSettings(MasterRenderer.NEAR_PLANE, MasterRenderer.FAR_PLANE);
		moveFactor += WAVE_SPEED * Engine.getDelta();
		moveFactor %= 1;
		shader.loadMoveFactor(moveFactor);
		shader.loadLight(sun);
		shader.loadSkyColor(skyColor);
		shader.loadFogSettings();

		// Binds the quad as the object
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);

		// Loads all of the textures for the shader
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());

		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);

		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap);

		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());

		// Enables texture blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	/* Disables shader settings */
	private void unbind() {
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	/* Loads the quad */
	private void setUpVAO() {
		// Just x and z vertex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = ModelManager.loadToVAO(vertices, 2);
	}

	/* Cleans up memory */
	public void cleanUp() {
		shader.cleanUp();
		fbos.cleanUp();
	}
}
